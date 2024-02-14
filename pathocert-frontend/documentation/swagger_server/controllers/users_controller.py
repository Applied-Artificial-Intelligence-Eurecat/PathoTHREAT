import connexion
import six

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.user import User  # noqa: E501
from swagger_server import util


def api_users_id_change_password_patch(token, id):  # noqa: E501
    """Change User Password

    Change the password of the authenticated user. # noqa: E501

    :param token: Token of the authenticated user to verify that the user is trying to change their own password and not somebody else&#x27;s.
    :type token: dict | bytes
    :param id: Numeric ID of the user to change the password. Needs to match with the authenticated user.
    :type id: int

    :rtype: User
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_users_my_user_post(token):  # noqa: E501
    """Get User Object

    Get the information of the authenticated user. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes

    :rtype: User
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
