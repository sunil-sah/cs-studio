package org.csstudio.utility.ldap.service;

import javax.annotation.Nonnull;

import org.csstudio.utility.treemodel.builder.IContentModelBuilder;

/**
 * The LDAP tree content model builder interface.
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 07.09.2010
 */
public interface ILdapContentModelBuilder extends IContentModelBuilder {

    /**
     * Sets the current search result for which a new model should be build or
     * an existing model should be enriched with.
     * @param result the LDAP search result
     */
    void setSearchResult(@Nonnull final ILdapSearchResult result);
}