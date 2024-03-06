// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: helloworld.proto

// Protobuf Java Version: 3.25.2
package io.grpc.examples.helloworld;

public interface MixRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:helloworld.MixRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Numeric Types
   * </pre>
   *
   * <code>double double_field = 1;</code>
   * @return The doubleField.
   */
  double getDoubleField();

  /**
   * <pre>
   * 67.89
   * </pre>
   *
   * <code>float float_field = 2;</code>
   * @return The floatField.
   */
  float getFloatField();

  /**
   * <pre>
   * 12345
   * </pre>
   *
   * <code>int32 int32_field = 3;</code>
   * @return The int32Field.
   */
  int getInt32Field();

  /**
   * <pre>
   * 9876543210L
   * </pre>
   *
   * <code>int64 int64_field = 4;</code>
   * @return The int64Field.
   */
  long getInt64Field();

  /**
   * <pre>
   * 54321
   * </pre>
   *
   * <code>uint32 uint32_field = 5;</code>
   * @return The uint32Field.
   */
  int getUint32Field();

  /**
   * <pre>
   *54332523521L
   * </pre>
   *
   * <code>uint64 uint64_field = 6;</code>
   * @return The uint64Field.
   */
  long getUint64Field();

  /**
   * <pre>
   * -50  
   * </pre>
   *
   * <code>sint32 sint32_field = 7;</code>
   * @return The sint32Field.
   */
  int getSint32Field();

  /**
   * <pre>
   *-9999999999L
   * </pre>
   *
   * <code>sint64 sint64_field = 8;</code>
   * @return The sint64Field.
   */
  long getSint64Field();

  /**
   * <pre>
   * 10000
   * </pre>
   *
   * <code>fixed32 fixed32_field = 9;</code>
   * @return The fixed32Field.
   */
  int getFixed32Field();

  /**
   * <pre>
   * 23523523532L
   * </pre>
   *
   * <code>fixed64 fixed64_field = 10;</code>
   * @return The fixed64Field.
   */
  long getFixed64Field();

  /**
   * <pre>
   * -200
   * </pre>
   *
   * <code>sfixed32 sfixed32_field = 11;</code>
   * @return The sfixed32Field.
   */
  int getSfixed32Field();

  /**
   * <pre>
   * -88888888L
   * </pre>
   *
   * <code>sfixed64 sfixed64_field = 12;</code>
   * @return The sfixed64Field.
   */
  long getSfixed64Field();

  /**
   * <pre>
   * 1
   * </pre>
   *
   * <code>bool bool_field = 13;</code>
   * @return The boolField.
   */
  boolean getBoolField();

  /**
   * <pre>
   * Textual Types
   * </pre>
   *
   * <code>string string_field = 14;</code>
   * @return The stringField.
   */
  java.lang.String getStringField();
  /**
   * <pre>
   * Textual Types
   * </pre>
   *
   * <code>string string_field = 14;</code>
   * @return The bytes for stringField.
   */
  com.google.protobuf.ByteString
      getStringFieldBytes();

  /**
   * <pre>
   * 1
   * </pre>
   *
   * <code>.helloworld.MixRequest.Status status = 16;</code>
   * @return The enum numeric value on the wire for status.
   */
  int getStatusValue();
  /**
   * <pre>
   * 1
   * </pre>
   *
   * <code>.helloworld.MixRequest.Status status = 16;</code>
   * @return The status.
   */
  io.grpc.examples.helloworld.MixRequest.Status getStatus();

  /**
   * <pre>
   * street: "123 Main St" city: "Mycity" zip_code: "12345"
   * </pre>
   *
   * <code>.helloworld.MixRequest.Address shipping_address = 17;</code>
   * @return Whether the shippingAddress field is set.
   */
  boolean hasShippingAddress();
  /**
   * <pre>
   * street: "123 Main St" city: "Mycity" zip_code: "12345"
   * </pre>
   *
   * <code>.helloworld.MixRequest.Address shipping_address = 17;</code>
   * @return The shippingAddress.
   */
  io.grpc.examples.helloworld.MixRequest.Address getShippingAddress();
  /**
   * <pre>
   * street: "123 Main St" city: "Mycity" zip_code: "12345"
   * </pre>
   *
   * <code>.helloworld.MixRequest.Address shipping_address = 17;</code>
   */
  io.grpc.examples.helloworld.MixRequest.AddressOrBuilder getShippingAddressOrBuilder();

  /**
   * <pre>
   * a: "a" b: "b" c: "c" d: "d"
   * </pre>
   *
   * <code>.helloworld.MixRequest.Abcd abcd = 18;</code>
   * @return Whether the abcd field is set.
   */
  boolean hasAbcd();
  /**
   * <pre>
   * a: "a" b: "b" c: "c" d: "d"
   * </pre>
   *
   * <code>.helloworld.MixRequest.Abcd abcd = 18;</code>
   * @return The abcd.
   */
  io.grpc.examples.helloworld.MixRequest.Abcd getAbcd();
  /**
   * <pre>
   * a: "a" b: "b" c: "c" d: "d"
   * </pre>
   *
   * <code>.helloworld.MixRequest.Abcd abcd = 18;</code>
   */
  io.grpc.examples.helloworld.MixRequest.AbcdOrBuilder getAbcdOrBuilder();

  /**
   * <pre>
   * ["a", "b", "c", "d"]
   * </pre>
   *
   * <code>repeated string repeated_field = 19;</code>
   * @return A list containing the repeatedField.
   */
  java.util.List<java.lang.String>
      getRepeatedFieldList();
  /**
   * <pre>
   * ["a", "b", "c", "d"]
   * </pre>
   *
   * <code>repeated string repeated_field = 19;</code>
   * @return The count of repeatedField.
   */
  int getRepeatedFieldCount();
  /**
   * <pre>
   * ["a", "b", "c", "d"]
   * </pre>
   *
   * <code>repeated string repeated_field = 19;</code>
   * @param index The index of the element to return.
   * @return The repeatedField at the given index.
   */
  java.lang.String getRepeatedField(int index);
  /**
   * <pre>
   * ["a", "b", "c", "d"]
   * </pre>
   *
   * <code>repeated string repeated_field = 19;</code>
   * @param index The index of the value to return.
   * @return The bytes of the repeatedField at the given index.
   */
  com.google.protobuf.ByteString
      getRepeatedFieldBytes(int index);

  /**
   * <pre>
   * [1, 2, 3, 4]
   * </pre>
   *
   * <code>repeated int32 repeated_int32_field = 20 [packed = true];</code>
   * @return A list containing the repeatedInt32Field.
   */
  java.util.List<java.lang.Integer> getRepeatedInt32FieldList();
  /**
   * <pre>
   * [1, 2, 3, 4]
   * </pre>
   *
   * <code>repeated int32 repeated_int32_field = 20 [packed = true];</code>
   * @return The count of repeatedInt32Field.
   */
  int getRepeatedInt32FieldCount();
  /**
   * <pre>
   * [1, 2, 3, 4]
   * </pre>
   *
   * <code>repeated int32 repeated_int32_field = 20 [packed = true];</code>
   * @param index The index of the element to return.
   * @return The repeatedInt32Field at the given index.
   */
  int getRepeatedInt32Field(int index);
}