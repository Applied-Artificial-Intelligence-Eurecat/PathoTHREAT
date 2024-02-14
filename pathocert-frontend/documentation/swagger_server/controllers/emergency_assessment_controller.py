import connexion
import six

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.document_combination import DocumentCombination  # noqa: E501
from swagger_server.models.document_similarity import DocumentSimilarity  # noqa: E501
from swagger_server import util


def api_assessment_emergency_id_close_assessments_get(token, emergency_id):  # noqa: E501
    """Get Close Assessments Of An Emergency

    The close assessments of an emergency are a list of how similar the emergency is to a document in the database. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes
    :param emergency_id: Numeric ID of the emergency to calculate the close assessments of.
    :type emergency_id: int

    :rtype: List[DocumentSimilarity]
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_assessment_merge_documents_post(body, token):  # noqa: E501
    """Merge Documents Into One

    Merge multiple documents by combinating their information, so they can be presented to the user. # noqa: E501

    :param body: The document titles in ArrayList form.
    :type body: List[]
    :param token: Token of the authenticated user.
    :type token: dict | bytes

    :rtype: DocumentCombination
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
