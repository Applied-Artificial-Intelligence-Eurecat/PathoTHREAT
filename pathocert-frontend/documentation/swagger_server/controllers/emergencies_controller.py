import connexion
import six

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server.models.emergency import Emergency  # noqa: E501
from swagger_server.models.emergency_multiselect_options import EmergencyMultiselectOptions  # noqa: E501
from swagger_server import util


def api_emergencies_my_get(token, archived):  # noqa: E501
    """Get Emergencies List

    Get the emergencies available to the user, depending on their organization. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes
    :param archived: Whether to return the ongoing emergencies of the organization, or the past emergencies (those that have already been archived).
    :type archived: bool

    :rtype: Emergency
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_emergencies_selectable_values_get(token):  # noqa: E501
    """Get Selectable Emergencies

    Return three lists of values to be displayed to the user in three diferent multiselect drop-downs in a page during the emergency report process. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes

    :rtype: EmergencyMultiselectOptions
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_emergencies_send_to_ware_post(token):  # noqa: E501
    """Send Emergency To PathoWARE

    Send the calculated emergency assessment to the PathoWARE system. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes

    :rtype: str
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
