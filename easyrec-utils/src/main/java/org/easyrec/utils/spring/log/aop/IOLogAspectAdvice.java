/*
 * Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.easyrec.utils.spring.log.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.easyrec.utils.spring.log.LoggerUtils;
import org.easyrec.utils.spring.log.annotation.IOLog;
import org.springframework.beans.factory.InitializingBean;

/**
 * Aspect that logs method calls by printing a method's signature and its result.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: sat-rsa $<br/>
 * $Date: 2011-08-12 18:40:54 +0200 (Fri, 12 Aug 2011) $<br/>
 * $Revision: 119 $</p>
 *
 * @author Stephan Zavrel
 */
public class IOLogAspectAdvice implements InitializingBean {
    private Log log = LogFactory.getLog(getClass());

    /**
     * Logs a method call and prints the arguments and the return value to the log level given in the
     * iol parameter.
     *
     * @param pjp the JoinPoint containing information about the intercepted method call
     * @param iol annotation for the IOLog aspect; contains info about the log level
     * @return the result of the method call
     * @throws Throwable
     */
    public Object logIO(ProceedingJoinPoint pjp, IOLog iol) throws Throwable {

        StringBuilder sb = new StringBuilder();

        String methodName = pjp.getSignature().getName();

        // parameter names only work with when compiled with AspectJ compiler
        //String[] params = ((MethodSignature)pjp.getSignature()).getParameterNames();

        Class<?>[] paramTypes = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        Class<?> returnType = ((MethodSignature) pjp.getSignature()).getReturnType();
        Object[] args = pjp.getArgs();

        Log logger = LogFactory.getLog(pjp.getTarget().getClass());

        Object o = pjp.proceed();
        if (!LoggerUtils.isLogLevelEnabled(logger, iol.value())) return o;
        sb.append(methodName).append(argsToString(paramTypes, args)).append(':').append(returnType.getName())
                .append('=').append(o);
        LoggerUtils.log(logger, iol.value(), sb.toString());
        return o;
    }

    /**
     * Generates the log output from the given arguments.
     *
     * @param paramTypes paramTypes
     * @param args       the arguments passed to the method
     * @return a String representation of the passed arguments as key/value pairs.
     */
    private String argsToString(Class<?>[] paramTypes, Object[] args) {

        StringBuilder sb = new StringBuilder();
        sb.append('(');
        //        for (int i=0; i < args.length; i++) {
        //            sb.append(args[i]);
        //            if (i < args.length - 1) {
        //              sb.append(',');
        //          } else
        //              sb.append(')');
        //        }
        //
        for (int i = 0; i < paramTypes.length; i++) {
            sb.append(paramTypes[i].getName()).append("=").append(args[i]);
            if (i < paramTypes.length - 1) {
                sb.append(",");
            } else {
                sb.append(")");
            }
        }
        return sb.toString();
    }


    public void afterPropertiesSet() throws Exception {
        if (log.isInfoEnabled()) {
            log.info(this.getClass().getSimpleName() + " Aspect initialized!");
        }
    }
}
