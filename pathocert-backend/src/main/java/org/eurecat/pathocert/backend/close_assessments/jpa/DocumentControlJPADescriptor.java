package org.eurecat.pathocert.backend.close_assessments.jpa;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentControl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class DocumentControlJPADescriptor extends AbstractTypeDescriptor<DocumentControl> {

    protected DocumentControlJPADescriptor() {
        super(DocumentControl.class, new ImmutableMutabilityPlan<>());
    }

    @Override
    public String toString(DocumentControl value) {
        return null;
    }

    @Override
    public DocumentControl fromString(String string) {
        return null;
    }

    @Override
    public <X> X unwrap(DocumentControl value, Class<X> type, WrapperOptions options) {
        return null;
    }

    @Override
    public <X> DocumentControl wrap(X value, WrapperOptions options) {
        return null;
    }

    @Override
    public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
        return null;
    }


}
