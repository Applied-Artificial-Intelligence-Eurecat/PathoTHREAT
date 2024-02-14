# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.user import User  # noqa: E501
from swagger_server.test import BaseTestCase


class TestUsersController(BaseTestCase):
    """UsersController integration test stubs"""

    def test_api_users_id_change_password_patch(self):
        """Test case for api_users_id_change_password_patch

        Change User Password
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/users/{id}/change-password'.format(id=56),
            method='PATCH',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_users_my_user_post(self):
        """Test case for api_users_my_user_post

        Get User Object
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/users/my-user',
            method='POST',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
