import connexion
import six

from swagger_server.models.authenticate_body import AuthenticateBody  # noqa: E501
from swagger_server.models.authenticate_token_body import AuthenticateTokenBody  # noqa: E501
from swagger_server.models.inline_response200 import InlineResponse200  # noqa: E501
from swagger_server import util


def api_authenticate_post(body):  # noqa: E501
    """Authenticate User

    Authenticate a user into the server. This is done together with the PathoWARE system, allowing for a single set of login credentials to be necessary to use all the tools in the PathoCERT ecosystem. # noqa: E501

    :param body: The credentials of the user in AuthenticateBody form.
    :type body: dict | bytes

    :rtype: InlineResponse200
    """
    if connexion.request.is_json:
        body = AuthenticateBody.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_authenticate_token_post(body):  # noqa: E501
    """Verify JWT Token

    Verifiy if a saved JWT Token created by the system is valid, taking into account if it contains correct authentication and if this authentication has expired. # noqa: E501

    :param body: The token of the user in map form.
    :type body: dict | bytes

    :rtype: bool
    """
    if connexion.request.is_json:
        body = AuthenticateTokenBody.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
