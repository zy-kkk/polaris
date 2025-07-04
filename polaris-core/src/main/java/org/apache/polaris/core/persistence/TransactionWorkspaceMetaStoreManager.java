/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.polaris.core.persistence;

import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.polaris.core.PolarisCallContext;
import org.apache.polaris.core.entity.LocationBasedEntity;
import org.apache.polaris.core.entity.PolarisBaseEntity;
import org.apache.polaris.core.entity.PolarisEntity;
import org.apache.polaris.core.entity.PolarisEntityCore;
import org.apache.polaris.core.entity.PolarisEntityId;
import org.apache.polaris.core.entity.PolarisEntitySubType;
import org.apache.polaris.core.entity.PolarisEntityType;
import org.apache.polaris.core.entity.PolarisPrivilege;
import org.apache.polaris.core.persistence.dao.entity.BaseResult;
import org.apache.polaris.core.persistence.dao.entity.ChangeTrackingResult;
import org.apache.polaris.core.persistence.dao.entity.CreateCatalogResult;
import org.apache.polaris.core.persistence.dao.entity.CreatePrincipalResult;
import org.apache.polaris.core.persistence.dao.entity.DropEntityResult;
import org.apache.polaris.core.persistence.dao.entity.EntitiesResult;
import org.apache.polaris.core.persistence.dao.entity.EntityResult;
import org.apache.polaris.core.persistence.dao.entity.EntityWithPath;
import org.apache.polaris.core.persistence.dao.entity.GenerateEntityIdResult;
import org.apache.polaris.core.persistence.dao.entity.ListEntitiesResult;
import org.apache.polaris.core.persistence.dao.entity.LoadGrantsResult;
import org.apache.polaris.core.persistence.dao.entity.LoadPolicyMappingsResult;
import org.apache.polaris.core.persistence.dao.entity.PolicyAttachmentResult;
import org.apache.polaris.core.persistence.dao.entity.PrincipalSecretsResult;
import org.apache.polaris.core.persistence.dao.entity.PrivilegeResult;
import org.apache.polaris.core.persistence.dao.entity.ResolvedEntityResult;
import org.apache.polaris.core.persistence.dao.entity.ScopedCredentialsResult;
import org.apache.polaris.core.persistence.pagination.PageToken;
import org.apache.polaris.core.policy.PolicyEntity;
import org.apache.polaris.core.policy.PolicyType;

/**
 * Wraps an existing impl of PolarisMetaStoreManager and delegates expected "read" operations
 * through to the wrapped instance while throwing errors on unexpected operations or enqueuing
 * expected write operations into a collection to be committed as a single atomic unit.
 *
 * <p>Note that as long as the server-side multi-commit transaction semantics are effectively only
 * SERIALIZABLE isolation (i.e. if we can resolve all UpdateRequirements "statically" before the set
 * of commits and translate these into an atomic collection of compare-and-swap operations to apply
 * the transaction), this workspace should also reject readEntity/loadEntity operations to avoid
 * implying that any reads from this transaction workspace include writes performed into this
 * transaction workspace that haven't yet been committed.
 *
 * <p>Not thread-safe; instances should only be used within a single request context and should not
 * be reused between requests.
 */
public class TransactionWorkspaceMetaStoreManager implements PolarisMetaStoreManager {
  private final PolarisMetaStoreManager delegate;

  // TODO: If we want to support the semantic of opening a transaction in which multiple
  // reads and writes occur on the same entities, where the reads are expected to see the writes
  // within the transaction workspace that haven't actually been committed, we can augment this
  // class by allowing these pendingUpdates to represent the latest state of the entity if we
  // also increment entityVersion. We'd need to store both a "latest view" of all updated entities
  // to serve reads within the same transaction while also storing the ordered list of
  // pendingUpdates that ultimately need to be applied in order within the real MetaStoreManager.
  private final List<EntityWithPath> pendingUpdates = new ArrayList<>();

  public TransactionWorkspaceMetaStoreManager(PolarisMetaStoreManager delegate) {
    this.delegate = delegate;
  }

  public List<EntityWithPath> getPendingUpdates() {
    return ImmutableList.copyOf(pendingUpdates);
  }

  @Override
  public BaseResult bootstrapPolarisService(@Nonnull PolarisCallContext callCtx) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "bootstrapPolarisService");
    return null;
  }

  @Override
  public BaseResult purge(@Nonnull PolarisCallContext callCtx) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "purge");
    return null;
  }

  @Override
  public EntityResult readEntityByName(
      @Nonnull PolarisCallContext callCtx,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisEntityType entityType,
      @Nonnull PolarisEntitySubType entitySubType,
      @Nonnull String name) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "readEntityByName");
    return null;
  }

  @Override
  public ListEntitiesResult listEntities(
      @Nonnull PolarisCallContext callCtx,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisEntityType entityType,
      @Nonnull PolarisEntitySubType entitySubType,
      @Nonnull PageToken pageToken) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "listEntities");
    return null;
  }

  @Override
  public GenerateEntityIdResult generateNewEntityId(@Nonnull PolarisCallContext callCtx) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "generateNewEntityId");
    return null;
  }

  @Override
  public CreatePrincipalResult createPrincipal(
      @Nonnull PolarisCallContext callCtx, @Nonnull PolarisBaseEntity principal) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "createPrincipal");
    return null;
  }

  @Override
  public PrincipalSecretsResult loadPrincipalSecrets(
      @Nonnull PolarisCallContext callCtx, @Nonnull String clientId) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadPrincipalSecrets");
    return null;
  }

  @Override
  public PrincipalSecretsResult rotatePrincipalSecrets(
      @Nonnull PolarisCallContext callCtx,
      @Nonnull String clientId,
      long principalId,
      boolean reset,
      @Nonnull String oldSecretHash) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "rotatePrincipalSecrets");
    return null;
  }

  @Override
  public CreateCatalogResult createCatalog(
      @Nonnull PolarisCallContext callCtx,
      @Nonnull PolarisBaseEntity catalog,
      @Nonnull List<PolarisEntityCore> principalRoles) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "createCatalog");
    return null;
  }

  @Override
  public EntityResult createEntityIfNotExists(
      @Nonnull PolarisCallContext callCtx,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisBaseEntity entity) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "createEntityIfNotExists");
    return null;
  }

  @Override
  public EntitiesResult createEntitiesIfNotExist(
      @Nonnull PolarisCallContext callCtx,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull List<? extends PolarisBaseEntity> entities) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "createEntitiesIfNotExist");
    return null;
  }

  @Override
  public EntityResult updateEntityPropertiesIfNotChanged(
      @Nonnull PolarisCallContext callCtx,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisBaseEntity entity) {
    pendingUpdates.add(new EntityWithPath(catalogPath, entity));
    return new EntityResult(entity);
  }

  @Override
  public EntitiesResult updateEntitiesPropertiesIfNotChanged(
      @Nonnull PolarisCallContext callCtx, @Nonnull List<EntityWithPath> entities) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "updateEntitiesPropertiesIfNotChanged");
    return null;
  }

  @Override
  public EntityResult renameEntity(
      @Nonnull PolarisCallContext callCtx,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisBaseEntity entityToRename,
      @Nullable List<PolarisEntityCore> newCatalogPath,
      @Nonnull PolarisEntity renamedEntity) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "renameEntity");
    return null;
  }

  @Override
  public DropEntityResult dropEntityIfExists(
      @Nonnull PolarisCallContext callCtx,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisBaseEntity entityToDrop,
      @Nullable Map<String, String> cleanupProperties,
      boolean cleanup) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "dropEntityIfExists");
    return null;
  }

  @Override
  public PrivilegeResult grantUsageOnRoleToGrantee(
      @Nonnull PolarisCallContext callCtx,
      @Nullable PolarisEntityCore catalog,
      @Nonnull PolarisEntityCore role,
      @Nonnull PolarisEntityCore grantee) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "grantUsageOnRoleToGrantee");
    return null;
  }

  @Override
  public PrivilegeResult revokeUsageOnRoleFromGrantee(
      @Nonnull PolarisCallContext callCtx,
      @Nullable PolarisEntityCore catalog,
      @Nonnull PolarisEntityCore role,
      @Nonnull PolarisEntityCore grantee) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "revokeUsageOnRoleFromGrantee");
    return null;
  }

  @Override
  public PrivilegeResult grantPrivilegeOnSecurableToRole(
      @Nonnull PolarisCallContext callCtx,
      @Nonnull PolarisEntityCore grantee,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisEntityCore securable,
      @Nonnull PolarisPrivilege privilege) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "grantPrivilegeOnSecurableToRole");
    return null;
  }

  @Override
  public PrivilegeResult revokePrivilegeOnSecurableFromRole(
      @Nonnull PolarisCallContext callCtx,
      @Nonnull PolarisEntityCore grantee,
      @Nullable List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisEntityCore securable,
      @Nonnull PolarisPrivilege privilege) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "revokePrivilegeOnSecurableFromRole");
    return null;
  }

  @Override
  public @Nonnull LoadGrantsResult loadGrantsOnSecurable(
      @Nonnull PolarisCallContext callCtx, PolarisEntityCore securable) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadGrantsOnSecurable");
    return null;
  }

  @Override
  public @Nonnull LoadGrantsResult loadGrantsToGrantee(
      @Nonnull PolarisCallContext callCtx, PolarisEntityCore grantee) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadGrantsToGrantee");
    return null;
  }

  @Override
  public ChangeTrackingResult loadEntitiesChangeTracking(
      @Nonnull PolarisCallContext callCtx, @Nonnull List<PolarisEntityId> entityIds) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadEntitiesChangeTracking");
    return null;
  }

  @Override
  public EntityResult loadEntity(
      @Nonnull PolarisCallContext callCtx,
      long entityCatalogId,
      long entityId,
      PolarisEntityType entityType) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "loadEntity");
    return null;
  }

  @Override
  public EntitiesResult loadTasks(
      @Nonnull PolarisCallContext callCtx, String executorId, PageToken pageToken) {
    callCtx.getDiagServices().fail("illegal_method_in_transaction_workspace", "loadTasks");
    return null;
  }

  @Override
  public ScopedCredentialsResult getSubscopedCredsForEntity(
      @Nonnull PolarisCallContext callCtx,
      long catalogId,
      long entityId,
      PolarisEntityType entityType,
      boolean allowListOperation,
      @Nonnull Set<String> allowedReadLocations,
      @Nonnull Set<String> allowedWriteLocations) {
    return delegate.getSubscopedCredsForEntity(
        callCtx,
        catalogId,
        entityId,
        entityType,
        allowListOperation,
        allowedReadLocations,
        allowedWriteLocations);
  }

  @Override
  public ResolvedEntityResult loadResolvedEntityById(
      @Nonnull PolarisCallContext callCtx,
      long entityCatalogId,
      long entityId,
      PolarisEntityType entityType) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadResolvedEntityById");
    return null;
  }

  @Override
  public ResolvedEntityResult loadResolvedEntityByName(
      @Nonnull PolarisCallContext callCtx,
      long entityCatalogId,
      long parentId,
      @Nonnull PolarisEntityType entityType,
      @Nonnull String entityName) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadResolvedEntityByName");
    return null;
  }

  @Override
  public ResolvedEntityResult refreshResolvedEntity(
      @Nonnull PolarisCallContext callCtx,
      int entityVersion,
      int entityGrantRecordsVersion,
      @Nonnull PolarisEntityType entityType,
      long entityCatalogId,
      long entityId) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "refreshResolvedEntity");
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public <T extends PolarisEntity & LocationBasedEntity>
      Optional<Optional<String>> hasOverlappingSiblings(
          @Nonnull PolarisCallContext callContext, T entity) {
    callContext
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "hasOverlappingSiblings");
    return Optional.empty();
  }

  @Override
  public @Nonnull PolicyAttachmentResult attachPolicyToEntity(
      @Nonnull PolarisCallContext callCtx,
      @Nonnull List<PolarisEntityCore> targetCatalogPath,
      @Nonnull PolarisEntityCore target,
      @Nonnull List<PolarisEntityCore> policyCatalogPath,
      @Nonnull PolicyEntity policy,
      Map<String, String> parameters) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "attachPolicyToEntity");
    return null;
  }

  @Override
  public @Nonnull PolicyAttachmentResult detachPolicyFromEntity(
      @Nonnull PolarisCallContext callCtx,
      @Nonnull List<PolarisEntityCore> catalogPath,
      @Nonnull PolarisEntityCore target,
      @Nonnull List<PolarisEntityCore> policyCatalogPath,
      @Nonnull PolicyEntity policy) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "detachPolicyFromEntity");
    return null;
  }

  @Override
  public @Nonnull LoadPolicyMappingsResult loadPoliciesOnEntity(
      @Nonnull PolarisCallContext callCtx, @Nonnull PolarisEntityCore target) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadPoliciesOnEntity");
    return null;
  }

  @Override
  public @Nonnull LoadPolicyMappingsResult loadPoliciesOnEntityByType(
      @Nonnull PolarisCallContext callCtx,
      @Nonnull PolarisEntityCore target,
      @Nonnull PolicyType policyType) {
    callCtx
        .getDiagServices()
        .fail("illegal_method_in_transaction_workspace", "loadPoliciesOnEntityByType");
    return null;
  }
}
