/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.phanos.testwedstrijdrunner.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.phanos.testwedstrijdrunner.entity.Onderdelen;
import nl.phanos.testwedstrijdrunner.entity.Sisresult;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author woutermkievit
 */
public class HibernateSessionHandler {

    private static HibernateSessionHandler THIS = null;
    private static boolean openSession = false;
    private Session session;

    public static HibernateSessionHandler get() {
        if (THIS == null) {
            THIS = new HibernateSessionHandler();
        }
        return THIS;
    }

    public Session getSession() {
        if (!openSession) {
            System.out.println("");
            openSession = true;
            session = HibernateUtil.getSessionFactory().openSession();
        }
        return session;
    }

    public List executeHQLQuery(String hql) {
        List resultList = null;
        try {
            Session session = getSession();
            session.beginTransaction();
            Query q = session.createQuery(hql);
            resultList = q.list();
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return resultList;
    }

    public <T> T getObject(String values, String columns, String type) {
        String query = "from " + type + " where ";
        for (int i = 0; i < columns.split(",").length; i++) {
            String column = columns.split(",")[i];
            String value = values.split(",")[i];
            query += (i == 0 ? " " : " AND ") + column + " = '" + value + "'";
        }

        System.out.println(query);
        return (T) executeHQLQuery(query).get(0);
    }

    public <T> T getObject(Class<T> T, String values, String columns) {
        return this.getObject(T, values.split(","), columns.split(","));
    }

    public <T> T getObject(Class<T> T, Object[] values, String[] columns) {
        Session session = null;
        try {
            session = getSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(T);
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].contains(".")) {
                    criteria.createAlias(columns[i].split("\\.")[0], columns[i].split("\\.")[0]);
                }
                criteria.add(Restrictions.eq(columns[i], values[i]));
            }
            System.out.println("search for "+T.getName());
            List list = criteria.list();

            System.out.println("found:" + list.size() + " " + T.getName());
            Object tmp = criteria.uniqueResult();
            return ((T) tmp);
        } catch (HibernateException he) {
            he.printStackTrace();
        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
        return null;

    }
    public void update(Object object) {
        Session session = null;
        try {
            session = getSession();
            session.beginTransaction();
            session.refresh(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }
    public void save(Object object) {
        System.out.println("saving:" + object.getClass());
        Session session = null;
        try {
            session = getSession();
            session.beginTransaction();
            session.saveOrUpdate(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }
    
    public Sisresult getClossedSisResult(int wedstrijdID,Date date) {
        Session session = null;
        Sisresult re=null;
        try {
            session = getSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Sisresult.class);
            criteria.add(Restrictions.eq("wedstrijden.id", wedstrijdID));
            criteria.add(Restrictions.le("time", date));
            criteria.addOrder(Order.desc("time") );
            if(criteria.list().size()>0){
                re=(Sisresult)criteria.list().get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
        return re;
    }

    public void delete(Object object) {
        Session session = null;
        try {
            session = getSession();
            session.beginTransaction();
            session.delete(object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

}
