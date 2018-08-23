FROM python:3

ARG timezone="America/Los_Angeles"

# Set the timezone
RUN echo $timezone | tee /etc/timezone && \
    ln -fs /usr/share/zoneinfo/${timezone} /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

WORKDIR /usr/src/app

USER nobody:nogroup

COPY ./integration_tests.py .

CMD [ "python", "./integration_tests.py", "-v", "-i", "configuration.json" ]
