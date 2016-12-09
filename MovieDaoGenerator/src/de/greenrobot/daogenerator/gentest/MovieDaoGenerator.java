/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.daogenerator.gentest;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates entities and DAOs
 * <p/>
 * Run it as a Java application (not Android).
 * If want to update schema without dropping table and want to preserve the data, can see this
 * http://stackoverflow.com/questions/16154113/greendao-schema-upgrade/
 * http://stackoverflow.com/questions/13373170/greendao-schema-update-and-data-migration
 *
 * @author HendraWD, hendraz_88@yahoo.co.id on 1/20/16
 */
public class MovieDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "hendrawd.ganteng.movieinfo.db");
        generateMovieDao(schema);
        new DaoGenerator().generateAll(schema, "/Users/ptinkosinarmedia/AndroidStudioProjects/MovieInfo/app/src/main/java/");
    }

    private static void generateMovieDao(Schema schema) {
        Entity movieEntity = schema.addEntity("Movie");
        movieEntity.addStringProperty("poster_path");
        movieEntity.addStringProperty("original_title");
        movieEntity.addStringProperty("title");
        movieEntity.addStringProperty("overview");
        movieEntity.addStringProperty("release_date");
        movieEntity.addStringProperty("backdrop_path");
        movieEntity.addFloatProperty("vote_average");
        movieEntity.addStringProperty("id").primaryKey();
        movieEntity.addStringProperty("genre_ids");
    }
}
