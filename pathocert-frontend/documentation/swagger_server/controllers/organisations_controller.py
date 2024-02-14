import connexion
import six

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.organization import Organization  # noqa: E501
from swagger_server import util


def api_users_my_organization_post(token):  # noqa: E501
    """Get Organisation Object

    Get the information of the authenticated user&#x27;s organization. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes

    :rtype: Organization
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
