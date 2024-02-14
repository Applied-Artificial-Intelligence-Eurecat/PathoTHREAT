# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.test import BaseTestCase


class TestExpertAskingController(BaseTestCase):
    """ExpertAskingController integration test stubs"""

    def test_api_expert_labels_get(self):
        """Test case for api_expert_labels_get

        Get All Labels In Expert Data
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/expert/labels',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_expert_labels_subject_get(self):
        """Test case for api_expert_labels_subject_get

        Get All Labels Connected To Subject In Expert Data
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/expert/labels/{subject}'.format(subject='subject_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_expert_search_terms_get(self):
        """Test case for api_expert_search_terms_get

        Get Expert Search Result
        """
        query_string = [('subject', 'subject_example'),
                        ('desired_output', 'desired_output_example')]
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/expert/search-terms',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_expert_values_get(self):
        """Test case for api_expert_values_get

        Get All Values In Expert Data
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/expert/values',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_expert_values_type_get(self):
        """Test case for api_expert_values_type_get

        Get All Values Of Certain Type In Expert Data
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/expert/values/{type}'.format(type='type_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
