package mobi.cangol.web.pecker.core.model;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@Data
@Entity
@Table(name = "APP")
public class App implements Serializable {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Date createTime;
    @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Date updateTime;
    @Column
    private String name;
    @Column
    private String identifier;
    @Column
    private String agent;
    @Column
    private Long  server;
    @Column
    private String alpha;
    @Column
    private String trial;
    @Column
    private String release;
}

