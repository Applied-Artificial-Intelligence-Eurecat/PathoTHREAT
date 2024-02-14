# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.document_combination import DocumentCombination  # noqa: E501
from swagger_server.models.document_similarity import DocumentSimilarity  # noqa: E501
from swagger_server.test import BaseTestCase


class TestEmergencyAssessmentController(BaseTestCase):
    """EmergencyAssessmentController integration test stubs"""

    def test_api_assessment_emergency_id_close_assessments_get(self):
        """Test case for api_assessment_emergency_id_close_assessments_get

        Get Close Assessments Of An Emergency
        """
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/assessment/{emergencyId}/close-assessments'.format(emergency_id=56),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_api_assessment_merge_documents_post(self):
        """Test case for api_assessment_merge_documents_post

        Merge Documents Into One
        """
        body = ['body_example']
        headers = [('token', AuthenticateHeader())]
        response = self.client.open(
            '/api/assessment/merge-documents',
            method='POST',
            data=json.dumps(body),
            headers=headers,
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
