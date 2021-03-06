/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.security.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.seedstack.seed.security.api.Domain;
import org.seedstack.seed.security.api.RealmProvider;
import org.seedstack.seed.security.api.Role;
import org.seedstack.seed.security.api.Scope;
import org.seedstack.seed.security.api.exceptions.AuthorizationException;
import org.seedstack.seed.security.api.principals.PrincipalProvider;
import org.seedstack.seed.security.api.principals.Principals;
import org.seedstack.seed.security.api.principals.SimplePrincipalProvider;
import org.seedstack.seed.security.internal.authorization.ScopePermission;
import org.seedstack.seed.security.internal.authorization.SeedAuthorizationInfo;
import org.seedstack.seed.security.internal.realms.ShiroRealmAdapter;

/**
 * Implementation of SecuritySupport for Shiro
 * 
 * @author yves.dautremay@mpsa.com
 */
public class ShiroSecuritySupport implements RealmProvider {

    @Inject
    private Set<Realm> realms;

    @Override
    public PrincipalProvider<?> getIdentityPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.getPrincipal() instanceof PrincipalProvider) {
            return (PrincipalProvider<?>) subject.getPrincipal();
        }
        return Principals.identityPrincipal("");
    }

    @Override
    public Collection<PrincipalProvider<?>> getOtherPrincipals() {
        Collection<PrincipalProvider<?>> principals = new ArrayList<PrincipalProvider<?>>();
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        if (principalCollection == null) {
            return Collections.emptyList();
        }
        for (Object shiroPrincipal : principalCollection.asList()) {
            if (shiroPrincipal instanceof PrincipalProvider) {
                principals.add((PrincipalProvider<?>) shiroPrincipal);
            }
        }
        return principals;
    }

    @Override
    public <T extends Serializable> Collection<PrincipalProvider<T>> getPrincipalsByType(Class<T> principalClass) {
        return Principals.getPrincipalsByType(getOtherPrincipals(), principalClass);
    }

    @Override
    public Collection<SimplePrincipalProvider> getSimplePrincipals() {
        return Principals.getSimplePrincipals(getOtherPrincipals());
    }

    @Override
    public SimplePrincipalProvider getSimplePrincipalByName(String principalName) {
        return Principals.getSimplePrincipalByName(getOtherPrincipals(), principalName);
    }

    @Override
    public boolean isAuthenticated() {
        return SecurityUtils.getSubject().isAuthenticated();
    }

    @Override
    public boolean isPermitted(String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }

    @Override
    public boolean isPermitted(String permission, Scope... scopes) {
        if (ArrayUtils.isEmpty(scopes)) {
            return isPermitted(permission);
        }
        boolean isPermitted = true;
        for (Scope scope : scopes) {
            isPermitted = isPermitted && SecurityUtils.getSubject().isPermitted(new ScopePermission(permission, scope));
        }
        return isPermitted;
    }

    @Override
    public boolean isPermittedAll(String... permissions) {
        return SecurityUtils.getSubject().isPermittedAll(permissions);
    }

    @Override
    public boolean isPermittedAny(String... permissions) {
        boolean[] bools = SecurityUtils.getSubject().isPermitted(permissions);
        return ArrayUtils.contains(bools, true);
    }

    @Override
    public void checkPermission(String permission) {
        try {
            SecurityUtils.getSubject().checkPermission(permission);
        } catch (org.apache.shiro.authz.AuthorizationException e) {
            throw new AuthorizationException("Subject doesn't have permission " + permission, e);
        }
    }

    @Override
    public void checkPermission(String permission, Scope... scopes) {
        try {
            if (ArrayUtils.isEmpty(scopes)) {
                checkPermission(permission);
            } else {
                for (Scope scope : scopes) {
                    SecurityUtils.getSubject().checkPermission(new ScopePermission(permission, scope));
                }
            }
        } catch (org.apache.shiro.authz.AuthorizationException e) {
            throw new AuthorizationException("Subject doesn't have permission " + permission, e);
        }
    }

    @Override
    public void checkPermissions(String... stringPermissions) {
        Collection<org.apache.shiro.authz.Permission> permissions = new ArrayList<org.apache.shiro.authz.Permission>();
        for (String stringPermission : stringPermissions) {
            permissions.add(new ScopePermission(stringPermission));
        }
        try {
            SecurityUtils.getSubject().checkPermissions(permissions);
        } catch (org.apache.shiro.authz.AuthorizationException e) {
            throw new AuthorizationException("Subject doesn't have permissions " + Arrays.toString(stringPermissions), e);
        }
    }

    @Override
    public boolean hasRole(String roleIdentifier) {
        return SecurityUtils.getSubject().hasRole(roleIdentifier);
    }

    @Override
    public boolean hasRole(String roleIdentifier, Scope... scopes) {
        if (ArrayUtils.isEmpty(scopes)) {
            return hasRole(roleIdentifier);
        }
        Role role = null;
        Set<Role> roles = getRoles();
        for (Role role2 : roles) {
            if (role2.getName().equals(roleIdentifier)) {
                role = role2;
                break;
            }
        }
        if (role == null) {
            return false;
        }
        return role.getScopes().containsAll(Arrays.asList(scopes));
    }

    @Override
    public boolean hasAllRoles(String... roleIdentifiers) {
        return SecurityUtils.getSubject().hasAllRoles(Arrays.asList(roleIdentifiers));
    }

    @Override
    public boolean hasAnyRole(String... roleIdentifiers) {
        boolean[] hasRoles = SecurityUtils.getSubject().hasRoles(Arrays.asList(roleIdentifiers));
        return ArrayUtils.contains(hasRoles, true);
    }

    @Override
    public void checkRole(String roleIdentifier) {
        try {
            SecurityUtils.getSubject().checkRole(roleIdentifier);
        } catch (org.apache.shiro.authz.AuthorizationException e) {
            throw new AuthorizationException("Subject doesn't have role " + roleIdentifier, e);
        }
    }

    @Override
    public void checkRoles(String... roleIdentifiers) {
        try {
            SecurityUtils.getSubject().checkRoles(roleIdentifiers);
        } catch (org.apache.shiro.authz.AuthorizationException e) {
            throw new AuthorizationException("Subject doesn't have roles " + Arrays.toString(roleIdentifiers), e);
        }
    }

    @Override
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    @Override
    @Deprecated
    public Set<org.seedstack.seed.security.api.Realm> provideRealms() {
        Set<org.seedstack.seed.security.api.Realm> result = new HashSet<org.seedstack.seed.security.api.Realm>();
        org.apache.shiro.mgt.SecurityManager securityManager = SecurityUtils.getSecurityManager();
        if (!(securityManager instanceof RealmSecurityManager)) {
            return Collections.emptySet();
        }
        Collection<Realm> shiroRealms = ((RealmSecurityManager) securityManager).getRealms();
        for (Realm shiroRealm : shiroRealms) {
            if (shiroRealm instanceof ShiroRealmAdapter) {
                result.add(((ShiroRealmAdapter) shiroRealm).getRealm());
            }
        }
        return result;
    }

    private SeedAuthorizationInfo getAuthorizationInfo(Realm realm) {
        SeedAuthorizationInfo authzInfo = null;
        if (realm instanceof ShiroRealmAdapter) {
            AuthorizationInfo tmpInfo = ((ShiroRealmAdapter) realm).getAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
            authzInfo = (SeedAuthorizationInfo) tmpInfo;
        }
        return authzInfo;
    }

    @Override
    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<Role>();
        for (Realm realm : realms) {
            SeedAuthorizationInfo authzInfo = getAuthorizationInfo(realm);
            if (authzInfo != null) {
                for (Role role : authzInfo.getSeedRoles()) {
                    roles.add(Role.unmodifiableRole(role));
                }
            }
        }
        return roles;
    }

    @Override
    public Set<Domain> getDomains() {
        Set<Domain> domains = new HashSet<Domain>();
        for (Role role : getRoles()) {
            domains.addAll(role.getScopesByType(Domain.class));
        }
        return domains;
    }

    @Override
    public String getHost() {
        Session s = SecurityUtils.getSubject().getSession(false);
        if (s == null) {
            return null;
        }
        return s.getHost();
    }
}
