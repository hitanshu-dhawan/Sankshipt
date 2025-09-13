package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity(name = "roles")
@Data
public class Role extends BaseModel {

    private String value;

}


/*

We do not have the APIs for adding the roles and assigning them to the users. So, we will have to add them manually in the database.

Queries to add the roles in the database:

INSERT INTO roles (created_at, updated_at, value)
VALUES
    (NOW(), NOW(), 'ADMIN'),
    (NOW(), NOW(), 'USER');

INSERT INTO users_roles (users_id, roles_id)
VALUES
    (1, 1), -- User 1 -> ADMIN
    (2, 2); -- User 2 -> USER

*/
