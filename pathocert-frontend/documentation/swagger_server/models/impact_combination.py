# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server import util


class ImpactCombination(Model):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """
    def __init__(self, people_ill: str=None, people_hospitalized: str=None, people_dead: str=None):  # noqa: E501
        """ImpactCombination - a model defined in Swagger

        :param people_ill: The people_ill of this ImpactCombination.  # noqa: E501
        :type people_ill: str
        :param people_hospitalized: The people_hospitalized of this ImpactCombination.  # noqa: E501
        :type people_hospitalized: str
        :param people_dead: The people_dead of this ImpactCombination.  # noqa: E501
        :type people_dead: str
        """
        self.swagger_types = {
            'people_ill': str,
            'people_hospitalized': str,
            'people_dead': str
        }

        self.attribute_map = {
            'people_ill': 'peopleIll',
            'people_hospitalized': 'peopleHospitalized',
            'people_dead': 'peopleDead'
        }
        self._people_ill = people_ill
        self._people_hospitalized = people_hospitalized
        self._people_dead = people_dead

    @classmethod
    def from_dict(cls, dikt) -> 'ImpactCombination':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The ImpactCombination of this ImpactCombination.  # noqa: E501
        :rtype: ImpactCombination
        """
        return util.deserialize_model(dikt, cls)

    @property
    def people_ill(self) -> str:
        """Gets the people_ill of this ImpactCombination.


        :return: The people_ill of this ImpactCombination.
        :rtype: str
        """
        return self._people_ill

    @people_ill.setter
    def people_ill(self, people_ill: str):
        """Sets the people_ill of this ImpactCombination.


        :param people_ill: The people_ill of this ImpactCombination.
        :type people_ill: str
        """

        self._people_ill = people_ill

    @property
    def people_hospitalized(self) -> str:
        """Gets the people_hospitalized of this ImpactCombination.


        :return: The people_hospitalized of this ImpactCombination.
        :rtype: str
        """
        return self._people_hospitalized

    @people_hospitalized.setter
    def people_hospitalized(self, people_hospitalized: str):
        """Sets the people_hospitalized of this ImpactCombination.


        :param people_hospitalized: The people_hospitalized of this ImpactCombination.
        :type people_hospitalized: str
        """

        self._people_hospitalized = people_hospitalized

    @property
    def people_dead(self) -> str:
        """Gets the people_dead of this ImpactCombination.


        :return: The people_dead of this ImpactCombination.
        :rtype: str
        """
        return self._people_dead

    @people_dead.setter
    def people_dead(self, people_dead: str):
        """Sets the people_dead of this ImpactCombination.


        :param people_dead: The people_dead of this ImpactCombination.
        :type people_dead: str
        """

        self._people_dead = people_dead
