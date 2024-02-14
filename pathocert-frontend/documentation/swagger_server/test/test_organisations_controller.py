# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.organization import Organization  # noqa: E501
from swagger_server.test import BaseTestCase


class TestOrganisationsController(BaseTestCase):
    """OrganisationsController integration test stubs"""

    def test_api_users_my_organization_post(self):
        """Test case for api_users_my_organization_post

        Get Organisation Object
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/users/my-organization',
            method='POST',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
