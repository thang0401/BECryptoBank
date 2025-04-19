package com.cryptobank.backend.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;
import vn.payos.type.WebhookData;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Jacksonized
public class Webhook {
	@NonNull
	  private String code;
	  @NonNull
	  private String desc;
	  private Boolean success;
	  @NonNull
	  private WebhookData data;
	  @NonNull
	  private String signature;
}
