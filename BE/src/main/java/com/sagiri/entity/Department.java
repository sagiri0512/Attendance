package com.sagiri.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Department {

  private Long id;
  private String deptName;
  private Long parentId;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;
  private Long deleted;
}
