/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery.storage.v1alpha2;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.cloud.bigquery.storage.v1alpha2.ProtoBufProto.ProtoSchema;
import io.grpc.Status;

import java.util.*;

// A converter class that prepares turn a native protobuf::DescriptorProto to a self contained protobuf::DescriptorProto
// that can be used by the service.
public class ProtoSchemaConverter {
	private class StructName {
		public String getName() { return "__S" + (count++); }
		private int count = 0;
	}
	ProtoSchema convertInternal(Descriptor input, List<String> visitedTypes, StructName structName) {
		DescriptorProto.Builder result = DescriptorProto.newBuilder();
		result.setName(structName.getName());
		visitedTypes.add(input.getFullName());
		for (int i = 0; i < input.getFields().size(); i++) {
			FieldDescriptor input_field = input.getFields().get(i);
			FieldDescriptorProto.Builder result_field = input_field.toProto().toBuilder();
			if (input_field.getType() == FieldDescriptor.Type.GROUP ||
			    input_field.getType() == FieldDescriptor.Type.MESSAGE) {
				if (visitedTypes.contains(input_field.getFullName())) {
					throw new InvalidArgumentException(
							"Recursive type is not supported", null, GrpcStatusCode.of(Status.Code.INVALID_ARGUMENT),
							false);
				}
				result.addNestedType(
						convertInternal(input_field.getMessageType(), visitedTypes, structName).getProtoDescriptor());
				visitedTypes.add(input_field.getFullName());
				result_field.setTypeName(result.getNestedType(result.getNestedTypeCount() - 1).getName());
			}
			if (input_field.getType() == FieldDescriptor.Type.ENUM) {
				result.addEnumType(input_field.getEnumType().toProto());
				result_field.setTypeName(result.getEnumType(result.getEnumTypeCount() - 1).getName());
			}
		}
		return ProtoSchema.newBuilder().setProtoDescriptor(result.build()).build();
	}
	ProtoSchema convert(Descriptor descriptor) {
		ArrayList<String> visitedTypes = new ArrayList<String>();
		StructName structName = new StructName();
		return convertInternal(descriptor, visitedTypes, structName);
	}



}
