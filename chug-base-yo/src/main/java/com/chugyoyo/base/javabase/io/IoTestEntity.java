package com.chugyoyo.base.javabase.io;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@AllArgsConstructor
public class IoTestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;
}
