package com.github.youchatproject.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/11
 * 包名： com.github.youchatproject.dao
 * 文档描述：e
 */
@Entity(
    active = true , nameInDb = "tbSystemInfo",
        indexes = {
                @Index(value = "systemPreferences DESC", unique = true)
        }
)
public class SystemInfos {
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String systemPreferences ;

    private String systemContentStr ;

    private int systemContentIte;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 275237384)
    private transient SystemInfosDao myDao;

    @Generated(hash = 124284313)
    public SystemInfos(Long id, String systemPreferences, String systemContentStr,
            int systemContentIte) {
        this.id = id;
        this.systemPreferences = systemPreferences;
        this.systemContentStr = systemContentStr;
        this.systemContentIte = systemContentIte;
    }

    @Generated(hash = 221011467)
    public SystemInfos() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSystemPreferences() {
        return this.systemPreferences;
    }

    public void setSystemPreferences(String systemPreferences) {
        this.systemPreferences = systemPreferences;
    }

    public String getSystemContentStr() {
        return this.systemContentStr;
    }

    public void setSystemContentStr(String systemContentStr) {
        this.systemContentStr = systemContentStr;
    }

    public int getSystemContentIte() {
        return this.systemContentIte;
    }

    public void setSystemContentIte(int systemContentIte) {
        this.systemContentIte = systemContentIte;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 558229466)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSystemInfosDao() : null;
    }
}
