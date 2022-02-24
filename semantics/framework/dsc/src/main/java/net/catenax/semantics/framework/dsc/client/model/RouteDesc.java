/*
 * Dataspace Connector
 * IDS Connector originally developed by the Fraunhofer ISST
 *
 * OpenAPI spec version: 6.2.0
 * Contact: info@dataspace-connector.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package net.catenax.semantics.framework.dsc.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * RouteDesc
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-08T16:15:16.333286600+02:00[Europe/Berlin]")
public class RouteDesc {
  @JsonProperty("title")
  private String title = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("configuration")
  private String _configuration = null;

  /**
   * Gets or Sets deploy
   */
  public enum DeployEnum {
    NONE("None"),
    CAMEL("Camel");

    private String value;

    DeployEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static DeployEnum fromValue(String text) {
      for (DeployEnum b : DeployEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("deploy")
  private DeployEnum deploy = null;

  public RouteDesc title(String title) {
    this.title = title;
    return this;
  }

   /**
   * Get title
   * @return title
  **/
  @Schema(description = "")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public RouteDesc description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @Schema(description = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public RouteDesc _configuration(String _configuration) {
    this._configuration = _configuration;
    return this;
  }

   /**
   * Get _configuration
   * @return _configuration
  **/
  @Schema(description = "")
  public String getConfiguration() {
    return _configuration;
  }

  public void setConfiguration(String _configuration) {
    this._configuration = _configuration;
  }

  public RouteDesc deploy(DeployEnum deploy) {
    this.deploy = deploy;
    return this;
  }

   /**
   * Get deploy
   * @return deploy
  **/
  @Schema(description = "")
  public DeployEnum getDeploy() {
    return deploy;
  }

  public void setDeploy(DeployEnum deploy) {
    this.deploy = deploy;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RouteDesc routeDesc = (RouteDesc) o;
    return Objects.equals(this.title, routeDesc.title) &&
        Objects.equals(this.description, routeDesc.description) &&
        Objects.equals(this._configuration, routeDesc._configuration) &&
        Objects.equals(this.deploy, routeDesc.deploy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, _configuration, deploy);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RouteDesc {\n");
    
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    _configuration: ").append(toIndentedString(_configuration)).append("\n");
    sb.append("    deploy: ").append(toIndentedString(deploy)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}