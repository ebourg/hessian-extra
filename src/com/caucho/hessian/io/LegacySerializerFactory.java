package com.caucho.hessian.io;

import java.util.Locale;

/**
 * SerializerFactory for {@link java.util.Locale} objects.
 * 
 * @author Emmanuel Bourg
 */
public class LegacySerializerFactory extends AbstractSerializerFactory
{
    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException
    {
        if (Locale.class.equals(cl))
        {
            return new LegacyLocaleSerializer();
        }

        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException
    {
        if (Locale.class.equals(cl))
        {
            return new LegacyLocaleDeserializer();
        }

        return null;
    }
}
