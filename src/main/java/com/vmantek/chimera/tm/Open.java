package com.vmantek.chimera.tm;

import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.transaction.Context;
import org.jpos.transaction.TxnSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.Serializable;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class Open extends TxnSupport
{
    int timeout = 0;

    PlatformTransactionManager tm;

    @Autowired
    public void setTransactionManager(PlatformTransactionManager tm)
    {
        this.tm = tm;
    }

    public int prepare(long id, Serializable o)
    {
        int rc = ABORTED;
        Context ctx = (Context) o;
        try
        {
            beginTransaction(ctx);
            checkPoint(ctx);
            rc = PREPARED;
        }
        catch (Throwable t)
        {
            error(t);
        }
        return rc | NO_JOIN | READONLY;
    }

    public void setConfiguration(Configuration cfg)
        throws ConfigurationException
    {
        super.setConfiguration(cfg);
        this.timeout = cfg.getInt("timeout", 0);
    }

    private void beginTransaction(Context ctx)
    {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        if (timeout > 0)
        {
            def.setTimeout(timeout);
        }
        TransactionStatus tx = tm.getTransaction(def);
        ctx.put(TX, tx);
    }

    public void commit(long id, Serializable o)
    {
    }

    public void abort(long id, Serializable o)
    {
    }
}
