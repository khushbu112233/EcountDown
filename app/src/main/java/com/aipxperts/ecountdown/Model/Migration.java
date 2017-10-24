package com.aipxperts.ecountdown.Model;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by aipxperts-ubuntu-01 on 15/9/17.
 */
public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {



            schema.create("Event")
                    .addField("_token", String.class)
                    .addField("event_uuid", String.class)
                    .addField("event_name", String.class)
                    .addField("event_description", String.class)
                    .addField("event_image", String.class)
                    .addField("is_cover", String.class)
                    .addField("date", String.class)
                    .addField("end_date", String.class)
                    .addField("isComplete", String.class)
                    .addField("created_date", String.class)
                    .addField("modified_date", String.class)
                    .addField("operation", String.class)
                    .addField("Category", String.class)
                    .addField("isFrom", String.class)
                    .addField("CategoryColor", String.class);

            schema.create("UProfile")
                    .addField("U_id", String.class)
                    .addField("U_Name", String.class)
                    .addField("U_Email", String.class)
                    .addField("U_MobileNo", String.class)
                    .addField("U_image", String.class)
                    .addField("U_Password", String.class)
                    .addField("U_Gender", String.class)
                    .addField("Age", String.class);

            schema.create("Category")
                    .addField("CategoryName", String.class)
                    .addField("CategoryId", String.class)
                    .addField("CategoryColor", String.class)
                    .addField("isNew", String.class)
                    .addField("isclick",Boolean.class)
                    .addField("selected", String.class);

            oldVersion++;
        }

        if (oldVersion == 1) {


            schema.get("Event");
            schema.get("Category");
            schema.get("UProfile");

            oldVersion++;
        }
       /* if(oldVersion==2)
        {
            schema.get("Event");
            schema.get("Category");
            schema.get("UProfile");

            schema.create("ShareEvent")
                    .addField("s_event_uuid", String.class)
                    .addField("s_event_name", String.class)
                    .addField("s_event_description", String.class)
                    .addField("s_event_image", String.class)
                    .addField("s_date", String.class)
                    .addField("s_Category", String.class)
                    .addField("s_CategoryColor", String.class);
        }*/

            /*if(oldVersion == 2){
                RealmObjectSchema sweatPointSchema = schema.get("Category");
                sweatPointSchema.addField("CategoryName",String.class);
                sweatPointSchema.addField("CategoryId",String.class);
                sweatPointSchema.addField("CategoryColor",String.class);
                sweatPointSchema.addField("isNew",String.class);
                sweatPointSchema.addField("selected",String.class);


                RealmObjectSchema eventObj = schema.get("Event");
                eventObj.addField("_token",String.class);
                eventObj.addField("event_uuid",String.class);
                eventObj.addField("event_name",String.class);
                eventObj.addField("event_description",String.class);
                eventObj.addField("event_image",String.class);
                eventObj.addField("is_cover",String.class);
                eventObj.addField("date",String.class);
                eventObj.addField("end_date",String.class);
                eventObj.addField("isComplete",String.class);
                eventObj.addField("created_date",String.class);
                eventObj.addField("modified_date",String.class);
                eventObj.addField("operation",String.class);
                eventObj.addField("Category",String.class);
                eventObj.addField("CategoryColor",String.class);







                RealmObjectSchema uProfileObj = schema.get("UProfile");
                uProfileObj.addField("U_id",String.class);
                uProfileObj.addField("U_Name",String.class);
                uProfileObj.addField("U_Email",String.class);
                uProfileObj.addField("U_MobileNo",String.class);
                uProfileObj.addField("U_image",String.class);
                uProfileObj.addField("U_Password",String.class);
                uProfileObj.addField("U_Gender",String.class);
                uProfileObj.addField("Age",String.class);

                oldVersion ++;
            }*/

            /*if(oldVersion == 3){
                RealmObjectSchema leaderboardInfoSchema = schema.get("LeaderBoardDb");
                leaderboardInfoSchema.addField("displayName",String.class);


                oldVersion ++;

            }*/

    }

    @Override
    public int hashCode() {
        return Migration.this.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Migration);
    }
}