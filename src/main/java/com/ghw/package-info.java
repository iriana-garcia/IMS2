@org.hibernate.annotations.TypeDefs({ @org.hibernate.annotations.TypeDef(name = "encryptedString", typeClass = EncryptedStringType.class, parameters = { @Parameter(name = "encryptorRegisteredName", value = "strongHibernateStringEncryptor") }) })
package com.ghw;

import org.hibernate.annotations.Parameter;
import org.jasypt.hibernate4.type.EncryptedStringType;

