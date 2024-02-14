package org.eurecat.pathocert.backend.close_assessments.jpa;

import org.eurecat.pathocert.backend.close_assessments.data.DocumentImpact;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class DocumentImpactJPADescriptor extends AbstractTypeDescriptor<DocumentImpact> {

    protected DocumentImpactJPADescriptor() {
        super(DocumentImpact.class, new ImmutableMutabilityPlan<>());
    }

    @Override
    public String toString(DocumentImpact value) {
        return null;
    }

    @Override
    public DocumentImpact fromString(String string) {
        return null;
    }

    @Override
    public <X> X unwrap(DocumentImpact value, Class<X> type, WrapperOptions options) {
        return null;
    }

    @Override
    public <X> DocumentImpact wrap(X value, WrapperOptions options) {
        return null;
    }

    @Override
    public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
        return null;
    }


}
