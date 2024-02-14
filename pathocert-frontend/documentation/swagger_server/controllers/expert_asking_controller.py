import connexion
import six

from swagger_server.models.authenticate_header import AuthenticateHeader  # noqa: E501
from swagger_server import util


def api_expert_labels_get(token):  # noqa: E501
    """Get All Labels In Expert Data

    Get all the labels found in the expert data. This is used to provide autocomplete functionality to the search component. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes

    :rtype: List[str]
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_expert_labels_subject_get(token, subject):  # noqa: E501
    """Get All Labels Connected To Subject In Expert Data

    Get a list of all the labels connected to the specified subject. This is used to provide extra functionalities to the autocomplete functionality in the search component. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes
    :param subject: Subject for which we want to check related labels.
    :type subject: str

    :rtype: List[str]
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_expert_search_terms_get(token, subject, desired_output):  # noqa: E501
    """Get Expert Search Result

    Search for information in the expert database. The endpoint receives a subject and a desired output, and returns the values found. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes
    :param subject: Subject of the query.
    :type subject: str
    :param desired_output: Type of the desired outputs.
    :type desired_output: str

    :rtype: str
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_expert_values_get(token):  # noqa: E501
    """Get All Values In Expert Data

    Get a list of all the values found in the expert database. This is used to provide autocomplete functionality to the search component. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes

    :rtype: List[str]
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def api_expert_values_type_get(token, type):  # noqa: E501
    """Get All Values Of Certain Type In Expert Data

    Get a list of all the values of a certain type found in the expert database. This is used to provide extra functionalities to the autocomplete functionality in the search component. # noqa: E501

    :param token: Token of the authenticated user.
    :type token: dict | bytes
    :param type: Type of the values the user is interested in.
    :type type: str

    :rtype: List[str]
    """
    if connexion.request.is_json:
        token = AuthenticateHeader.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
