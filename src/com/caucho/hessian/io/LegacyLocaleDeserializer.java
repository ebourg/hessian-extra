package com.caucho.hessian.io;

import java.io.IOException;
import java.util.Locale;

/**
 * Locale deserializer consistent with the default serializer under Java 6.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class LegacyLocaleDeserializer extends AbstractDeserializer
{
    public Class getType()
    {
        return Locale.class;
    }

    public Object readMap(AbstractHessianInput in) throws IOException
    {
        String language = null;
        String country = null;
        String variant = null;

        while (!in.isEnd())
        {
            String key = in.readString();

            if (key.equals("language"))
            {
                language = in.readString();
            }
            else if (key.equals("country"))
            {
                country = in.readString();
            }
            else if (key.equals("variant"))
            {
                variant = in.readString();
            }
            else
            {
                // ignore the value
                in.readObject();
            }
        }

        in.readMapEnd();

        Locale obj = create(language, country, variant);

        in.addRef(obj);

        return obj;
    }

    public Object readObject(AbstractHessianInput in, Object[] fieldNames) throws IOException
    {
        String language = null;
        String country = null;
        String variant = null;

        for (Object fieldName : fieldNames)
        {
            String key = (String) fieldName;

            if (key.equals("language"))
            {
                language = in.readString();
            }
            else if (key.equals("country"))
            {
                country = in.readString();
            }
            else if (key.equals("variant"))
            {
                variant = in.readString();
            }
            else
            {
                // ignore the value
                in.readObject();
            }
        }

        Locale obj = create(language, country, variant);

        in.addRef(obj);

        return obj;
    }

    private Locale create(String language, String country, String variant)
    {
        if (variant != null)
        {
            return new Locale(language, country, variant);
        }
        else if (country != null)
        {
            return new Locale(language, country);
        }
        else
        {
            return new Locale(language);
        }
    }
}
