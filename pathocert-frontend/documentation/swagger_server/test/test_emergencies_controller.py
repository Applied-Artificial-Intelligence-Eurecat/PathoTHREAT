# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.emergency import Emergency  # noqa: E501
from swagger_server.models.emergency_multiselect_options import EmergencyMultiselectOptions  # noqa: E501
from swagger_server.test import BaseTestCase


class TestEmergenciesController(BaseTestCase):
    """EmergenciesController integration test stubs"""

    def test_api_emergencies_my_get(self):
        """Test case for api_emergencies_my_get

        Get Emergencies List
        """
        query_string = [('archived', true)]
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/emergencies/my',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_emergencies_selectable_values_get(self):
        """Test case for api_emergencies_selectable_values_get

        Get Selectable Emergencies
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/emergencies/selectable-values',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_emergencies_send_to_ware_post(self):
        """Test case for api_emergencies_send_to_ware_post

        Send Emergency To PathoWARE
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/emergencies/send-to-ware',
            method='POST',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
