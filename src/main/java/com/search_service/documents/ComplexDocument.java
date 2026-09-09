package com.search_service.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "complexes")
@Setting(settingPath = "/elastic/settings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplexDocument {

    @Id
    @Field(type = FieldType.Long)
    private Long id;
    @Field(type = FieldType.Long)
    private Long locationId;
    @Field(type = FieldType.Long)
    private Long districtId;
    @Field(type = FieldType.Long)
    private Long developerId;
    @Field(type = FieldType.Long)
    private List<Long> metroStationId;


    @Field(type = FieldType.Text, analyzer = "russian_search", searchAnalyzer = "fuzzy_analyzer")
    private String location;

    @Field(type = FieldType.Text, analyzer = "russian_search", searchAnalyzer = "fuzzy_analyzer")
    private String district;

    @Field(type = FieldType.Text, analyzer = "russian_search", searchAnalyzer = "fuzzy_analyzer")
    private String name;

    @Field(type = FieldType.Text, analyzer = "russian_search", searchAnalyzer = "fuzzy_analyzer")
    private String developer;

    @Field(type = FieldType.Text, analyzer = "russian_search", searchAnalyzer = "fuzzy_analyzer")
    private List<String> metroStation;

}

