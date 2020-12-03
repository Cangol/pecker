package mobi.cangol.web.pecker.core.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "APP_SERVER")
public class AppServer implements Serializable {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Date createTime;
    @Column(columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Date updateTime;
    @Column
    private String type;
    @Column
    private String host;
    @Column
    private Integer port;
    @Column
    private String domain;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String repo;
    @Column
    private String token;
}

