import unittest
import requests
import argparse
import json
import sys
import logging
from datetime import date, datetime
from urllib import parse
from random import randint


class integration_tests(unittest.TestCase):

    # Set class variables related to configurations for tests
    @classmethod
    def set_configuration_variables(cls, configuration):
        config_file_raw = open(configuration)
        config_file_json = json.load(config_file_raw)

        base_url = config_file_json["hostname"] + "v1/hr"
        cls.departments_url = base_url + "/departments"
        cls.positions_url = base_url + "/positions"
        cls.locations_url = base_url + "/locations"

        # This should be coming from a /businesscenters endpoint
        cls.business_centers = ["AABC", "AMBC", "ASBC", "BEBC",
                                "CCBO", "FOBC", "HSBC", "UABC"]

        # Only student positions are currently supported.
        cls.position_type = "student"

        # Minimal number of locations to count for
        cls.minimal_location_count = 200

        client_id = config_file_json["client_id"]
        client_secret = config_file_json["client_secret"]

        access_token = cls.get_access_token(
                config_file_json["token_api"],
                client_id,
                client_secret)

        # Set headers and query parameters
        cls.auth_header = {'Authorization': 'Bearer %s' % access_token}

    # Helper method to get an access token
    @staticmethod
    def get_access_token(url, client_id, client_secret):
        post_data = {
            'client_id': client_id,
            'client_secret': client_secret,
            'grant_type': 'client_credentials'}

        request = requests.post(url, data=post_data)
        response = request.json()

        return response["access_token"]

    def __make_request(self, url, params=None):
        return requests.get(url, params=params, headers=self.auth_header)

    def __departments_request(self, business_center):
        params = {'businessCenter': business_center}
        return self.__make_request(self.departments_url, params)

    def test_departments(self):
        for bc in self.business_centers:
            logging.debug("Testing business center: {}".format(bc))
            request = self.__departments_request(bc)

            request_elapsed_seconds = request.elapsed.total_seconds()
            logging.debug("Request took {} second(s)"
                          .format(request_elapsed_seconds))

            self.assertEqual(request.status_code, 200)
            self.assertLess(request_elapsed_seconds, 1)

            departments = request.json()["data"]
            self.assertGreaterEqual(len(departments), 1)

            for department in departments:
                id = department["id"]
                logging.debug("Testing department ID: {}".format(id))
                attributes = department["attributes"]

                self.assertEqual(department["type"], "department")
                self.assertIsNotNone(id)
                self.assertIsNotNone(attributes["name"])
                self.assertEqual(id, attributes["organizationCode"])
                self.assertEqual(bc, attributes["businessCenter"])

    def test_departments_bad_requests(self):
        self.assertEqual(self.__departments_request(None).status_code, 400)
        self.assertEqual(self.__departments_request("Bad bc").status_code, 400)

    def __positions_request(self, business_center, position_type):
        params = {'businessCenter': business_center,
                  'type': position_type}
        return self.__make_request(self.positions_url, params)

    def test_positions(self):
        for bc in self.business_centers:
            logging.debug("Testing business center: {}".format(bc))

            departments = self.__departments_request(bc).json()
            department_codes = set([department["id"]
                                   for department in departments["data"]])

            logging.debug("Department codes: {}".format(department_codes))

            request = self.__positions_request(bc, self.position_type)
            request_elapsed_seconds = request.elapsed.total_seconds()
            logging.debug("Request took {} second(s)"
                          .format(request_elapsed_seconds))

            self.assertEqual(request.status_code, 200)
            self.assertLess(request_elapsed_seconds, 1)

            positions = request.json()["data"]
            self.assertGreaterEqual(len(positions), 1)

            for position in positions:
                id = position["id"]
                logging.debug("Testing position ID: {}".format(id))
                attributes = position["attributes"]

                self.assertEqual(position["type"], "position")
                self.assertIsNotNone(id)
                self.assertIsNotNone(attributes["title"])
                self.assertIsNotNone(attributes["class"])
                self.assertEqual(attributes["businessCenter"], bc)
                self.assertTrue(attributes["organizationCode"]
                                in department_codes)

                low_salary = attributes["lowSalaryPoint"]
                high_salary = attributes["highSalaryPoint"]
                self.assertIsNotNone(low_salary)
                self.assertIsNotNone(high_salary)
                self.assertGreater(high_salary, low_salary)

    def test_position_type_case(self):
        request = self.__positions_request(self.business_centers[0],
                                           self.position_type.upper())
        self.assertEqual(request.status_code, 200)

    def test_positions_bad_requests(self):
        self.assertEqual(self.__positions_request(
                         None, self.position_type).status_code, 400)
        self.assertEqual(self.__positions_request(
                         "Bad bc", self.position_type).status_code, 400)
        self.assertEqual(self.__positions_request(
                         self.business_centers[0],
                         "classified").status_code, 400)

    def __locations_request(self, state, date=None):
        params = {'state': state, 'date': date}
        return self.__make_request(self.locations_url, params)

    def __location_request(self, location_id, date=None):
        params = {'date': date}
        url = "{}/{}".format(self.locations_url, location_id)
        return self.__make_request(url, params)

    @staticmethod
    def get_date_from_self_link(self_link):
        self_link_query_params = parse.parse_qs(
                parse.urlsplit(self_link).query)
        return self_link_query_params["date"][0]

    @staticmethod
    def get_random_date():
        today = datetime.today()
        today_plus_4_years = datetime(today.year + 4, today.month, today.day)
        random_datetime = datetime.fromtimestamp(
                randint(int(today.timestamp()), int(today_plus_4_years
                        .timestamp())))

        logging.debug("Random date: {}".format(random_datetime))

        return random_datetime.date()

    def test_all_locations(self):
        request = self.__locations_request(None)
        request_elapsed_seconds = request.elapsed.total_seconds()
        logging.debug("Request took {} second(s)"
                      .format(request_elapsed_seconds))

        self.assertEqual(request.status_code, 200)
        self.assertLess(request_elapsed_seconds, 1)

        current_date = date.today().isoformat()
        logging.debug("Current date: {}".format(current_date))

        locations = request.json()["data"]
        self.assertGreaterEqual(len(locations), self.minimal_location_count)

        for location in locations:
            id = location["id"]
            logging.debug("Testing location ID: {}".format(id))
            attributes = location["attributes"]
            self.assertIsNotNone(id)
            self.assertIsNotNone(attributes["name"])
            self.assertEqual(location["type"], "Location")

            if attributes["state"] == "Oregon":
                # Minimum wage data only available for Oregon.
                logging.debug("Location is in Oregon")
                self.assertIsNotNone(attributes["minimumWage"])

            date_from_self_link = self.get_date_from_self_link(
                    location["links"]["self"])
            self.assertEqual(date_from_self_link, current_date)

            single_location_request = self.__location_request(id)
            single_location = single_location_request.json()
            self.assertEqual(single_location_request.status_code, 200)
            self.assertEqual(single_location["data"], location)

    def test_state_parameter(self):
        test_states = {'OR': 'Oregon', 'CA': 'California', 'WA': 'Washington'}

        for state_code, state in test_states.items():
            logging.debug("Testing state: {}".format(state))

            locations = self.__locations_request(state_code).json()["data"]
            self.assertGreaterEqual(len(locations), 1)
            for location in locations:
                logging.debug("Testing location ID: {}".format(location["id"]))
                attributes = location["attributes"]
                self.assertEqual(attributes["state"], state)
                self.assertEqual(attributes["stateCode"], state_code)

    def test_date_parameter(self):
        bad_date_request = self.__locations_request(None, "12/31/2018")
        self.assertEqual(bad_date_request.status_code, 400)

        random_date = self.get_random_date().isoformat()
        locations = self.__locations_request(None, random_date).json()["data"]

        self.assertGreaterEqual(len(locations), self.minimal_location_count)

        for location in locations:
            logging.debug("Testing location ID: {}".format(location["id"]))
            self_link_date = self.get_date_from_self_link(
                    location["links"]["self"])
            self.assertEqual(self_link_date, random_date)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
            description='HR API integration tests'
            )
    parser.add_argument(
            '--config', '-i',
            dest='config_file',
            help='Path to configuration file containing API credentials',
            required=True
            )
    parser.add_argument(
            '--debug',
            dest='debug',
            help='Enable debug logging mode',
            action='store_true'
            )
    arguments, unittest_args = parser.parse_known_args()

    if arguments.debug:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.WARNING)

    # Load configuration file
    config_file = arguments.config_file
    integration_tests.set_configuration_variables(config_file)

    unittest.main(argv=sys.argv[:1] + unittest_args)
