package org.eurecat.pathocert.backend.close_assessments.jpa;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BitTypeDescriptor;

public class DocumentControlJPA extends AbstractSingleColumnStandardBasicType<DocumentControl> {
    public DocumentControlJPA() {
        super(new BitTypeDescriptor(), new DocumentControlJPADescriptor());
    }

    @Override
    public String getName() {
        return "DocumentControlJPA";
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        return null;
    }

}
