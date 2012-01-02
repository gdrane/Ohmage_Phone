// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: edu/ucla/cens/pdc/libpdc/util/EncryptedMessage.proto

package edu.ucla.cens.pdc.libpdc.util;

public final class EncryptedMessage {
  private EncryptedMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }
  public interface SymmetricOrBuilder
      extends com.google.protobuf.MessageLiteOrBuilder {
    
    // required bytes cipher_text = 1;
    boolean hasCipherText();
    com.google.protobuf.ByteString getCipherText();
    
    // optional bytes iv = 2;
    boolean hasIv();
    com.google.protobuf.ByteString getIv();
  }
  public static final class Symmetric extends
      com.google.protobuf.GeneratedMessageLite
      implements SymmetricOrBuilder {
    // Use Symmetric.newBuilder() to construct.
    private Symmetric(Builder builder) {
      super(builder);
    }
    private Symmetric(boolean noInit) {}
    
    private static final Symmetric defaultInstance;
    public static Symmetric getDefaultInstance() {
      return defaultInstance;
    }
    
    public Symmetric getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    private int bitField0_;
    // required bytes cipher_text = 1;
    public static final int CIPHER_TEXT_FIELD_NUMBER = 1;
    private com.google.protobuf.ByteString cipherText_;
    public boolean hasCipherText() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public com.google.protobuf.ByteString getCipherText() {
      return cipherText_;
    }
    
    // optional bytes iv = 2;
    public static final int IV_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString iv_;
    public boolean hasIv() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    public com.google.protobuf.ByteString getIv() {
      return iv_;
    }
    
    private void initFields() {
      cipherText_ = com.google.protobuf.ByteString.EMPTY;
      iv_ = com.google.protobuf.ByteString.EMPTY;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasCipherText()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, cipherText_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, iv_);
      }
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, cipherText_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, iv_);
      }
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static Symmetric parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static Symmetric parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static Symmetric parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static Symmetric parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static Symmetric parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static Symmetric parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static Symmetric parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static Symmetric parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static Symmetric parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static Symmetric parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(EncryptedMessage.Symmetric prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          EncryptedMessage.Symmetric, Builder>
        implements EncryptedMessage.SymmetricOrBuilder {
      // Construct using EncryptedMessage.Symmetric.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        cipherText_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        iv_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public Symmetric getDefaultInstanceForType() {
        return EncryptedMessage.Symmetric.getDefaultInstance();
      }
      
      public Symmetric build() {
        EncryptedMessage.Symmetric result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private Symmetric buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        EncryptedMessage.Symmetric result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public Symmetric buildPartial() {
        EncryptedMessage.Symmetric result = new EncryptedMessage.Symmetric(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.cipherText_ = cipherText_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.iv_ = iv_;
        result.bitField0_ = to_bitField0_;
        return result;
      }
      
      public Builder mergeFrom(EncryptedMessage.Symmetric other) {
        if (other == EncryptedMessage.Symmetric.getDefaultInstance()) return this;
        if (other.hasCipherText()) {
          setCipherText(other.getCipherText());
        }
        if (other.hasIv()) {
          setIv(other.getIv());
        }
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCipherText()) {
          
          return false;
        }
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              
              return this;
            default: {
              if (!parseUnknownField(input, extensionRegistry, tag)) {
                
                return this;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              cipherText_ = input.readBytes();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              iv_ = input.readBytes();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required bytes cipher_text = 1;
      private com.google.protobuf.ByteString cipherText_ = com.google.protobuf.ByteString.EMPTY;
      public boolean hasCipherText() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public com.google.protobuf.ByteString getCipherText() {
        return cipherText_;
      }
      public Builder setCipherText(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        cipherText_ = value;
        
        return this;
      }
      public Builder clearCipherText() {
        bitField0_ = (bitField0_ & ~0x00000001);
        cipherText_ = getDefaultInstance().getCipherText();
        
        return this;
      }
      
      // optional bytes iv = 2;
      private com.google.protobuf.ByteString iv_ = com.google.protobuf.ByteString.EMPTY;
      public boolean hasIv() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      public com.google.protobuf.ByteString getIv() {
        return iv_;
      }
      public Builder setIv(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        iv_ = value;
        
        return this;
      }
      public Builder clearIv() {
        bitField0_ = (bitField0_ & ~0x00000002);
        iv_ = getDefaultInstance().getIv();
        
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:Symmetric)
    }
    
    static {
      defaultInstance = new Symmetric(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:Symmetric)
  }
  
  public interface SignedInterestOrBuilder
      extends com.google.protobuf.MessageLiteOrBuilder {
    
    // required string command = 1;
    boolean hasCommand();
    String getCommand();
    
    // required bytes nonce = 2;
    boolean hasNonce();
    com.google.protobuf.ByteString getNonce();
    
    // required bytes crc = 3;
    boolean hasCrc();
    com.google.protobuf.ByteString getCrc();
  }
  public static final class SignedInterest extends
      com.google.protobuf.GeneratedMessageLite
      implements SignedInterestOrBuilder {
    // Use SignedInterest.newBuilder() to construct.
    private SignedInterest(Builder builder) {
      super(builder);
    }
    private SignedInterest(boolean noInit) {}
    
    private static final SignedInterest defaultInstance;
    public static SignedInterest getDefaultInstance() {
      return defaultInstance;
    }
    
    public SignedInterest getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    private int bitField0_;
    // required string command = 1;
    public static final int COMMAND_FIELD_NUMBER = 1;
    private java.lang.Object command_;
    public boolean hasCommand() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public String getCommand() {
      java.lang.Object ref = command_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (com.google.protobuf.Internal.isValidUtf8(bs)) {
          command_ = s;
        }
        return s;
      }
    }
    private com.google.protobuf.ByteString getCommandBytes() {
      java.lang.Object ref = command_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8((String) ref);
        command_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    
    // required bytes nonce = 2;
    public static final int NONCE_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString nonce_;
    public boolean hasNonce() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    public com.google.protobuf.ByteString getNonce() {
      return nonce_;
    }
    
    // required bytes crc = 3;
    public static final int CRC_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString crc_;
    public boolean hasCrc() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    public com.google.protobuf.ByteString getCrc() {
      return crc_;
    }
    
    private void initFields() {
      command_ = "";
      nonce_ = com.google.protobuf.ByteString.EMPTY;
      crc_ = com.google.protobuf.ByteString.EMPTY;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasCommand()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasNonce()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasCrc()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getCommandBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, nonce_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, crc_);
      }
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getCommandBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, nonce_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, crc_);
      }
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static SignedInterest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static SignedInterest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static SignedInterest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static SignedInterest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static SignedInterest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static SignedInterest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static SignedInterest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static SignedInterest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static SignedInterest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static SignedInterest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(EncryptedMessage.SignedInterest prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          EncryptedMessage.SignedInterest, Builder>
        implements EncryptedMessage.SignedInterestOrBuilder {
      // Construct using EncryptedMessage.SignedInterest.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private void maybeForceBuilderInitialization() {
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        command_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        nonce_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        crc_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public SignedInterest getDefaultInstanceForType() {
        return EncryptedMessage.SignedInterest.getDefaultInstance();
      }
      
      public SignedInterest build() {
        EncryptedMessage.SignedInterest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private SignedInterest buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        EncryptedMessage.SignedInterest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public SignedInterest buildPartial() {
        EncryptedMessage.SignedInterest result = new EncryptedMessage.SignedInterest(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.command_ = command_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.nonce_ = nonce_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.crc_ = crc_;
        result.bitField0_ = to_bitField0_;
        return result;
      }
      
      public Builder mergeFrom(EncryptedMessage.SignedInterest other) {
        if (other == EncryptedMessage.SignedInterest.getDefaultInstance()) return this;
        if (other.hasCommand()) {
          setCommand(other.getCommand());
        }
        if (other.hasNonce()) {
          setNonce(other.getNonce());
        }
        if (other.hasCrc()) {
          setCrc(other.getCrc());
        }
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasCommand()) {
          
          return false;
        }
        if (!hasNonce()) {
          
          return false;
        }
        if (!hasCrc()) {
          
          return false;
        }
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              
              return this;
            default: {
              if (!parseUnknownField(input, extensionRegistry, tag)) {
                
                return this;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              command_ = input.readBytes();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              nonce_ = input.readBytes();
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              crc_ = input.readBytes();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required string command = 1;
      private java.lang.Object command_ = "";
      public boolean hasCommand() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public String getCommand() {
        java.lang.Object ref = command_;
        if (!(ref instanceof String)) {
          String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
          command_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      public Builder setCommand(String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        command_ = value;
        
        return this;
      }
      public Builder clearCommand() {
        bitField0_ = (bitField0_ & ~0x00000001);
        command_ = getDefaultInstance().getCommand();
        
        return this;
      }
      void setCommand(com.google.protobuf.ByteString value) {
        bitField0_ |= 0x00000001;
        command_ = value;
        
      }
      
      // required bytes nonce = 2;
      private com.google.protobuf.ByteString nonce_ = com.google.protobuf.ByteString.EMPTY;
      public boolean hasNonce() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      public com.google.protobuf.ByteString getNonce() {
        return nonce_;
      }
      public Builder setNonce(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        nonce_ = value;
        
        return this;
      }
      public Builder clearNonce() {
        bitField0_ = (bitField0_ & ~0x00000002);
        nonce_ = getDefaultInstance().getNonce();
        
        return this;
      }
      
      // required bytes crc = 3;
      private com.google.protobuf.ByteString crc_ = com.google.protobuf.ByteString.EMPTY;
      public boolean hasCrc() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      public com.google.protobuf.ByteString getCrc() {
        return crc_;
      }
      public Builder setCrc(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        crc_ = value;
        
        return this;
      }
      public Builder clearCrc() {
        bitField0_ = (bitField0_ & ~0x00000004);
        crc_ = getDefaultInstance().getCrc();
        
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:SignedInterest)
    }
    
    static {
      defaultInstance = new SignedInterest(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:SignedInterest)
  }
  
  
  static {
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
