import unittest
import requests
import argparse
import json
import sys


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

        cls.business_centers = ["AABC", "AMBC", "ASBC", "BEBC",
                                "CCBO", "FOBC", "HSBC", "UABC"]

        # Only student positions are currently supported.
        cls.position_type = "student"

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
            request = self.__departments_request(bc)

            self.assertEqual(request.status_code, 200)
            self.assertLess(request.elapsed.total_seconds(), 1)

            for department in request.json()["data"]:
                id = department["id"]
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
            departments = self.__departments_request(bc).json()
            department_codes = set([department["id"]
                                   for department in departments["data"]])

            request = self.__positions_request(bc, self.position_type)

            self.assertEqual(request.status_code, 200)
            self.assertLess(request.elapsed.total_seconds(), 2)

            for position in request.json()["data"]:
                id = position["id"]
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

    def __locations_request(self, state, date):
        params = {'state': state, 'date': date}
        return self.__make_request(self.locations_url, params)

    def __location_request(self, location_id, date):
        params = {'date': date}
        url = "{}/{}".format(self.locations_url, location_id)
        return self.__make_request(url, params)

    def test_all_locations(self):
        request = self.__locations_request(None, None)

        self.assertEqual(request.status_code, 200)
        self.assertLess(request.elapsed.total_seconds(), 1)

        for location in request.json()["data"]:
            self.assertIsNotNone(location["id"])
            self.assertIsNotNone(location["attributes"]["name"])
            self.assertEqual(location["type"], "Location")

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
    arguments, unittest_args = parser.parse_known_args()

    # Load configuration file
    config_file = arguments.config_file
    integration_tests.set_configuration_variables(config_file)

    unittest.main(argv=sys.argv[:1] + unittest_args)
