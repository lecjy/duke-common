package com.duke.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity<T> implements Serializable {
    private static final long serialVersionUID = -6751846719593132836L;
//    @JsonSerialize(using = StringSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private T id;
    private Date createTime;
    private Date updateTime;
    private String creator;
    private String updator;
    private Boolean deleted;
    private Integer version;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal tenantId;
}
