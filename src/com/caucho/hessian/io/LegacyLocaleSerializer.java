/*
 * Copyright (c) 2001-2004 Caucho Technology, Inc.  All rights reserved.
 *
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Caucho Technology (http://www.caucho.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Hessian", "Resin", and "Caucho" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    info@caucho.com.
 *
 * 5. Products derived from this software may not be called "Resin"
 *    nor may "Resin" appear in their names without prior written
 *    permission of Caucho Technology.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL CAUCHO TECHNOLOGY OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Scott Ferguson
 */

package com.caucho.hessian.io;

import java.io.IOException;
import java.util.Locale;

/**
 * Locale serializer consistent with the default serializer under Java 6.
 * 
 * <p>The {@link LocaleSerializer} isn't registered by default with the {@link SerializerFactory},
 * so if users didn't register it explicitly the default field based Java serializer was used.
 * Up to Java 6 the {@link java.util.Locale} class contained 3 fields (language, country and variant)
 * but this changed in Java 7 and the fields were replaced by a sun.util.locale.BaseLocale instance
 * wrapping them. This change induced a serialization incompatibility between clients and servers
 * using a mix of Java 6 and 7, or code deserializing classes previously serialized with Java 6.</p>
 * 
 * <p>This serializer restores the backward compatibility of serialized Locale objects by outputting
 * the 3 fields used before Java 7.</p>
 *
 * @author Emmanuel Bourg
 */
public class LegacyLocaleSerializer extends AbstractSerializer
{
    private static LegacyLocaleSerializer SERIALIZER = new LegacyLocaleSerializer();

    public static LegacyLocaleSerializer create()
    {
        return SERIALIZER;
    }

    public void writeObject(Object obj, AbstractHessianOutput out)
            throws IOException
    {
        if (obj == null)
        {
            out.writeNull();
        }
        else
        {
            Locale locale = (Locale) obj;

            if (out.addRef(obj))
            {
                return;
            }

            int ref = out.writeObjectBegin(locale.getClass().getName());

            if (ref < -1)
            {
                // Hessian 1
                if (locale.getLanguage() != null)
                {
                    out.writeString("language");
                    out.writeString(locale.getLanguage());
                }
                if (locale.getCountry() != null)
                {
                    out.writeString("country");
                    out.writeString(locale.getCountry());
                }
                if (locale.getVariant() != null)
                {
                    out.writeString("variant");
                    out.writeString(locale.getVariant());
                }
                out.writeMapEnd();
            }
            else
            {
                // Hessian 2
                if (ref == -1)
                {
                    // New class, write the definition
                    out.writeInt(3);
                    out.writeString("language");
                    out.writeString("country");
                    out.writeString("variant");
                    out.writeObjectBegin(locale.getClass().getName());
                }

                out.writeString(locale.getLanguage());
                out.writeString(locale.getCountry());
                out.writeString(locale.getVariant());
            }
        }
    }
}
