# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.authenticate_body import AuthenticateBody  # noqa: E501
from swagger_server.models.authenticate_token_body import AuthenticateTokenBody  # noqa: E501
from swagger_server.models.inline_response200 import InlineResponse200  # noqa: E501
from swagger_server.test import BaseTestCase


class TestAuthenticationController(BaseTestCase):
    """AuthenticationController integration test stubs"""

    def test_api_authenticate_post(self):
        """Test case for api_authenticate_post

        Authenticate User
        """
        body = AuthenticateBody()
        response = self.client.open(
            '/api/authenticate',
            method='POST',
            data=json.dumps(body),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_authenticate_token_post(self):
        """Test case for api_authenticate_token_post

        Verify JWT Token
        """
        body = AuthenticateTokenBody()
        response = self.client.open(
            '/api/authenticate/token',
            method='POST',
            data=json.dumps(body),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
