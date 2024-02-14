package org.eurecat.pathocert.backend.close_assessments.jpa;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BitTypeDescriptor;

public class DocumentImpactJPA extends AbstractSingleColumnStandardBasicType<DocumentImpact> {
    public DocumentImpactJPA() {
        super(new BitTypeDescriptor(), new DocumentImpactJPADescriptor());
    }

    @Override
    public String getName() {
        return "DocumentImpactJPA";
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        return null;
    }

}
