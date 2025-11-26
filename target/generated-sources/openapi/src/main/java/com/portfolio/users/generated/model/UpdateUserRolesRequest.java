package com.portfolio.users.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateUserRolesRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-25T11:40:36.904905+01:00[Europe/Madrid]", comments = "Generator version: 7.5.0")
public class UpdateUserRolesRequest {

  @Valid
  private List<@Size(min = 2)String> roles = new ArrayList<>();

  public UpdateUserRolesRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateUserRolesRequest(List<@Size(min = 2)String> roles) {
    this.roles = roles;
  }

  public UpdateUserRolesRequest roles(List<@Size(min = 2)String> roles) {
    this.roles = roles;
    return this;
  }

  public UpdateUserRolesRequest addRolesItem(String rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * Get roles
   * @return roles
  */
  @NotNull 
  @Schema(name = "roles", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roles")
  public List<@Size(min = 2)String> getRoles() {
    return roles;
  }

  public void setRoles(List<@Size(min = 2)String> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateUserRolesRequest updateUserRolesRequest = (UpdateUserRolesRequest) o;
    return Objects.equals(this.roles, updateUserRolesRequest.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateUserRolesRequest {\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

