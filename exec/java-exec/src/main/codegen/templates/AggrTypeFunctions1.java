/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
<@pp.dropOutputFile />



<#list aggrtypes1.aggrtypes as aggrtype>
<@pp.changeOutputFile name="/org/apache/drill/exec/expr/fn/impl/gaggr/${aggrtype.className}Functions.java" />

<#include "/@includes/license.ftl" />

<#-- A utility class that is used to generate java code for aggr functions that maintain a single -->
<#-- running counter to hold the result.  This includes: MIN, MAX, SUM, COUNT. -->

/* 
 * This class is automatically generated from AggrTypeFunctions1.tdd using FreeMarker.
 */

package org.apache.drill.exec.expr.fn.impl.gaggr;

import org.apache.drill.exec.expr.DrillAggFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.FunctionTemplate.FunctionScope;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.annotations.Workspace;
import org.apache.drill.exec.expr.holders.*;
import org.apache.drill.exec.record.RecordBatch;

@SuppressWarnings("unused")

public class ${aggrtype.className}Functions {
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(${aggrtype.className}Functions.class);

<#list aggrtype.types as type>

@FunctionTemplate(name = "${aggrtype.funcName}", scope = FunctionTemplate.FunctionScope.POINT_AGGREGATE)
public static class ${type.inputType}${aggrtype.className} implements DrillAggFunc{

  @Param ${type.inputType}Holder in;
  @Workspace ${type.runningType}Holder value;
  @Output ${type.outputType}Holder out;

  public void setup(RecordBatch b) {
	value = new ${type.runningType}Holder();  
	<#if aggrtype.funcName == "sum" || aggrtype.funcName == "count">
	  value.value = 0;
	<#elseif aggrtype.funcName == "min">
    <#if type.runningType?starts_with("Bit")>
        value.value = 1;
	  <#elseif type.runningType?starts_with("Int")>
	    value.value = Integer.MAX_VALUE;
	  <#elseif type.runningType?starts_with("BigInt")>
	    value.value = Long.MAX_VALUE;
	  <#elseif type.runningType?starts_with("Float4")>
		value.value = Float.MAX_VALUE;
	  <#elseif type.runningType?starts_with("Float8")>
		value.value = Double.MAX_VALUE;	    
	  </#if>
	<#elseif aggrtype.funcName == "max">
    <#if type.runningType?starts_with("Bit")>
        value.value = 0;
	  <#elseif type.runningType?starts_with("Int")>
	    value.value = Integer.MIN_VALUE;
	  <#elseif type.runningType?starts_with("BigInt")>
	    value.value = Long.MIN_VALUE;
	  <#elseif type.runningType?starts_with("Float4")>
		value.value = Float.MIN_VALUE;
	  <#elseif type.runningType?starts_with("Float8")>
		value.value = Double.MIN_VALUE;	    
	  </#if>
	</#if>
	  
  }
  
  @Override
  public void add() {
	  <#if type.inputType?starts_with("Nullable")>
	    sout: {
	    if (in.isSet == 0) {
		    // processing nullable input and the value is null, so don't do anything...
		    break sout;
	    }  
	  </#if>
	  <#if aggrtype.funcName == "min">
	    value.value = Math.min(value.value, in.value);
	  <#elseif aggrtype.funcName == "max">
	    value.value = Math.max(value.value,  in.value);
	  <#elseif aggrtype.funcName == "sum">
	    value.value += in.value;
	  <#elseif aggrtype.funcName == "count">
	    value.value++;
	  <#else>
	  // TODO: throw an error ? 
	  </#if>
	<#if type.inputType?starts_with("Nullable")>
    } // end of sout block
	</#if>
  }

  @Override
  public void output() {
    out.value = value.value;
  }

  @Override
  public void reset() {
	
	<#if aggrtype.funcName == "sum" || aggrtype.funcName == "count">
	  value.value = 0;
	<#elseif aggrtype.funcName == "min">
	  <#if type.runningType?starts_with("Int")>
	    value.value = Integer.MAX_VALUE;
	  <#elseif type.runningType?starts_with("BigInt")>
	    value.value = Long.MAX_VALUE;
	  <#elseif type.runningType?starts_with("Float4")>
		value.value = Float.MAX_VALUE;
	  <#elseif type.runningType?starts_with("Float8")>
		value.value = Double.MAX_VALUE;	    
	  </#if>
	<#elseif aggrtype.funcName == "max">
	  <#if type.runningType?starts_with("Int")>
	    value.value = Integer.MIN_VALUE;
	  <#elseif type.runningType?starts_with("BigInt")>
	    value.value = Long.MIN_VALUE;
	  <#elseif type.runningType?starts_with("Float4")>
		value.value = Float.MIN_VALUE;
	  <#elseif type.runningType?starts_with("Float8")>
		value.value = Double.MIN_VALUE;	    
	  </#if>
	</#if>
	  
  }
 
 }


</#list>
}
</#list>

