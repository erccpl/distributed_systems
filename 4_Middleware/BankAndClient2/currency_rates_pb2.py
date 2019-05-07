# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: currency_rates.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf.internal import enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='currency_rates.proto',
  package='',
  syntax='proto3',
  serialized_options=_b('\n\017currency_server'),
  serialized_pb=_b('\n\x14\x63urrency_rates.proto\"U\n\x0cSubscription\x12\x1f\n\x0chomeCurrency\x18\x01 \x01(\x0e\x32\t.Currency\x12$\n\x11\x66oreignCurrencies\x18\x02 \x03(\x0e\x32\t.Currency\";\n\rCurrencyValue\x12\x1b\n\x08\x63urrency\x18\x01 \x01(\x0e\x32\t.Currency\x12\r\n\x05value\x18\x02 \x01(\x02\",\n\x0bRatesUpdate\x12\x1d\n\x05rates\x18\x01 \x03(\x0b\x32\x0e.CurrencyValue*D\n\x08\x43urrency\x12\x0b\n\x07UNKNOWN\x10\x00\x12\x07\n\x03PLN\x10\x01\x12\x07\n\x03\x45UR\x10\x02\x12\x07\n\x03\x43HF\x10\x03\x12\x07\n\x03\x43\x41\x44\x10\x04\x12\x07\n\x03USD\x10\x05\x32\x43\n\x13\x45xchangeRateService\x12,\n\tsubscribe\x12\r.Subscription\x1a\x0c.RatesUpdate\"\x00\x30\x01\x42\x11\n\x0f\x63urrency_serverb\x06proto3')
)

_CURRENCY = _descriptor.EnumDescriptor(
  name='Currency',
  full_name='Currency',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='UNKNOWN', index=0, number=0,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='PLN', index=1, number=1,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='EUR', index=2, number=2,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='CHF', index=3, number=3,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='CAD', index=4, number=4,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='USD', index=5, number=5,
      serialized_options=None,
      type=None),
  ],
  containing_type=None,
  serialized_options=None,
  serialized_start=218,
  serialized_end=286,
)
_sym_db.RegisterEnumDescriptor(_CURRENCY)

Currency = enum_type_wrapper.EnumTypeWrapper(_CURRENCY)
UNKNOWN = 0
PLN = 1
EUR = 2
CHF = 3
CAD = 4
USD = 5



_SUBSCRIPTION = _descriptor.Descriptor(
  name='Subscription',
  full_name='Subscription',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='homeCurrency', full_name='Subscription.homeCurrency', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='foreignCurrencies', full_name='Subscription.foreignCurrencies', index=1,
      number=2, type=14, cpp_type=8, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=24,
  serialized_end=109,
)


_CURRENCYVALUE = _descriptor.Descriptor(
  name='CurrencyValue',
  full_name='CurrencyValue',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='currency', full_name='CurrencyValue.currency', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='value', full_name='CurrencyValue.value', index=1,
      number=2, type=2, cpp_type=6, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=111,
  serialized_end=170,
)


_RATESUPDATE = _descriptor.Descriptor(
  name='RatesUpdate',
  full_name='RatesUpdate',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='rates', full_name='RatesUpdate.rates', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=172,
  serialized_end=216,
)

_SUBSCRIPTION.fields_by_name['homeCurrency'].enum_type = _CURRENCY
_SUBSCRIPTION.fields_by_name['foreignCurrencies'].enum_type = _CURRENCY
_CURRENCYVALUE.fields_by_name['currency'].enum_type = _CURRENCY
_RATESUPDATE.fields_by_name['rates'].message_type = _CURRENCYVALUE
DESCRIPTOR.message_types_by_name['Subscription'] = _SUBSCRIPTION
DESCRIPTOR.message_types_by_name['CurrencyValue'] = _CURRENCYVALUE
DESCRIPTOR.message_types_by_name['RatesUpdate'] = _RATESUPDATE
DESCRIPTOR.enum_types_by_name['Currency'] = _CURRENCY
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Subscription = _reflection.GeneratedProtocolMessageType('Subscription', (_message.Message,), dict(
  DESCRIPTOR = _SUBSCRIPTION,
  __module__ = 'currency_rates_pb2'
  # @@protoc_insertion_point(class_scope:Subscription)
  ))
_sym_db.RegisterMessage(Subscription)

CurrencyValue = _reflection.GeneratedProtocolMessageType('CurrencyValue', (_message.Message,), dict(
  DESCRIPTOR = _CURRENCYVALUE,
  __module__ = 'currency_rates_pb2'
  # @@protoc_insertion_point(class_scope:CurrencyValue)
  ))
_sym_db.RegisterMessage(CurrencyValue)

RatesUpdate = _reflection.GeneratedProtocolMessageType('RatesUpdate', (_message.Message,), dict(
  DESCRIPTOR = _RATESUPDATE,
  __module__ = 'currency_rates_pb2'
  # @@protoc_insertion_point(class_scope:RatesUpdate)
  ))
_sym_db.RegisterMessage(RatesUpdate)


DESCRIPTOR._options = None

_EXCHANGERATESERVICE = _descriptor.ServiceDescriptor(
  name='ExchangeRateService',
  full_name='ExchangeRateService',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=288,
  serialized_end=355,
  methods=[
  _descriptor.MethodDescriptor(
    name='subscribe',
    full_name='ExchangeRateService.subscribe',
    index=0,
    containing_service=None,
    input_type=_SUBSCRIPTION,
    output_type=_RATESUPDATE,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_EXCHANGERATESERVICE)

DESCRIPTOR.services_by_name['ExchangeRateService'] = _EXCHANGERATESERVICE

# @@protoc_insertion_point(module_scope)
