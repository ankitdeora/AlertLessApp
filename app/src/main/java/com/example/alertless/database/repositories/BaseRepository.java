package com.example.alertless.database.repositories;

import android.app.Application;

import com.example.alertless.database.AppDatabase;
import com.example.alertless.database.dao.BaseDao;
import com.example.alertless.entities.BaseEntity;
import com.example.alertless.exceptions.AlertlessDatabaseException;
import com.example.alertless.exceptions.AlertlessException;
import com.example.alertless.models.BaseModel;
import com.example.alertless.utils.DBUtils;
import com.example.alertless.utils.ValidationUtils;

import java.util.Date;
import java.util.List;

public abstract class BaseRepository<Entity extends BaseEntity, Model extends BaseModel> {

    protected final AppDatabase appDatabase;
    protected BaseDao<Entity, Model> dao;

    protected BaseRepository(Application application) {
        appDatabase = AppDatabase.getDatabase(application);
    }

    public List<Entity> getAllEntities() throws AlertlessDatabaseException {
        return DBUtils.executeTaskAndGet(dao::findAllEntities, "Could not get all entities !!!");
    }

    protected Entity getEntity(Model model) throws AlertlessException {
        ValidationUtils.validateInput(model);

        String errMsg = String.format("Could not get DB Entity for model : %s !!!", model);
        return DBUtils.executeTaskAndGet(dao::findEntity, model, errMsg);
    }

    protected Entity getEntity(String id) throws AlertlessException {
        ValidationUtils.validateInput(id);

        String errMsg = String.format("Could not get Entity with id : %s !!!", id);
        return DBUtils.executeTaskAndGet(dao::findEntity, id, errMsg);
    }

    public void createEntity(Model model) throws AlertlessException {
        ValidationUtils.validateInput(model);
        final Entity existingEntity = getEntity(model);

        if (existingEntity != null) {
            String errMsg = String.format("Entity model : %s already exist !!!", model);
            throw new AlertlessDatabaseException(errMsg);
        }

        String uniqueId = String.valueOf(new Date().getTime());

        Entity entity = model.getEntity(uniqueId);

        String errMsg = String.format("Error while creating Entity for model : %s !!!", model);
        DBUtils.executeTask(dao::insert, entity, errMsg);
    }

    public void updateEntity(String id, Model updatedModel) throws AlertlessException {
        ValidationUtils.validateInput(id);
        ValidationUtils.validateInput(updatedModel);

        final Entity existingEntity = getEntity(id);

        if (existingEntity == null) {
            String errMsg = String.format("Entity with id : %s does not exist !!!", id);
            throw new AlertlessDatabaseException(errMsg);
        }

        Entity entityWithGivenModel = getEntity(updatedModel);

        if (entityWithGivenModel != null && !entityWithGivenModel.getId().equals(id)) {
            String errMsg = String.format("Entity : %s already exist for id : %s i.e. different from : %s !!!",
                    updatedModel, entityWithGivenModel.getId(), id);
            throw new AlertlessDatabaseException(errMsg);
        }

        Entity updatedEntity = updatedModel.getEntity(id);

        String errMsg = String.format("Error caught while updating Entity : %s for id : %s !!!", updatedModel, id);
        DBUtils.executeTask(dao::update, updatedEntity, errMsg);
    }

    public void deleteEntity(Model model) throws AlertlessException {
        ValidationUtils.validateInput(model);
        final Entity existingEntity = getEntity(model);

        if (existingEntity == null) {
            String errMsg = String.format("Delete failed as Entity for model : %s does not exist !!!", model);
            throw new AlertlessDatabaseException(errMsg);
        }

        String errMsg = String.format("Delete failed for Entity with model : %s !!!", model);
        DBUtils.executeTask(dao::delete, existingEntity, errMsg);

    }

    public void deleteEntity(String id) throws AlertlessException {
        ValidationUtils.validateInput(id);

        final Entity existingEntity = getEntity(id);

        if (existingEntity == null) {
            String errMsg = String.format("Delete failed as Entity with id : %s does not exist !!!", id);
            throw new AlertlessDatabaseException(errMsg);
        }

        String errMsg = String.format("Delete failed for Entity with id : %s !!!", id);
        DBUtils.executeTask(dao::delete, existingEntity, errMsg);
    }
}