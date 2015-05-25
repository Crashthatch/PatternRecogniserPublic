<object-stream>
    <TreeModel id="1" serialization="custom">
        <com.rapidminer.operator.AbstractIOObject>
            <default>
                <source>Decision Tree</source>
            </default>
        </com.rapidminer.operator.AbstractIOObject>
        <com.rapidminer.operator.ResultObjectAdapter>
            <default>
                <annotations id="2">
                    <keyValueMap id="3">
                        <entry>
                            <string>Source</string>
                            <string>//Rapidminer5Repository/patternMetaModelRelationship/model-decision-tree</string>
                        </entry>
                    </keyValueMap>
                </annotations>
            </default>
        </com.rapidminer.operator.ResultObjectAdapter>
        <com.rapidminer.operator.AbstractModel>
            <default>
                <headerExampleSet id="4" serialization="custom">
                    <com.rapidminer.operator.ResultObjectAdapter>
                        <default>
                            <annotations id="5">
                                <keyValueMap id="6"/>
                            </annotations>
                        </default>
                    </com.rapidminer.operator.ResultObjectAdapter>
                    <com.rapidminer.example.set.AbstractExampleSet>
                        <default>
                            <idMap id="7"/>
                            <statisticsMap id="8"/>
                        </default>
                    </com.rapidminer.example.set.AbstractExampleSet>
                    <com.rapidminer.example.set.HeaderExampleSet>
                        <default>
                            <attributes class="SimpleAttributes" id="9">
                                <attributes class="linked-list" id="10">
                                    <AttributeRole id="11">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="12" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="13">
                                                        <keyValueMap id="14"/>
                                                    </annotations>
                                                    <attributeDescription id="15">
                                                        <name>label_notNullRowsInTable</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>5</index>
                                                    </attributeDescription>
                                                    <constructionDescription>label_notNullRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="16">
                                                        <NumericalStatistics id="17">
                                                            <sum>30023.0</sum>
                                                            <squaredSum>8465921.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="18">
                                                            <sum>30023.0</sum>
                                                            <squaredSum>8465921.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="19">
                                                            <minimum>3.0</minimum>
                                                            <maximum>1623.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="20">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="21"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="22">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="23" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="24">
                                                        <keyValueMap id="25"/>
                                                    </annotations>
                                                    <attributeDescription id="26">
                                                        <name>label_uniqueRowsInTable</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>6</index>
                                                    </attributeDescription>
                                                    <constructionDescription>label_uniqueRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="27">
                                                        <NumericalStatistics id="28">
                                                            <sum>16042.0</sum>
                                                            <squaredSum>5942464.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="29">
                                                            <sum>16042.0</sum>
                                                            <squaredSum>5942464.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="30">
                                                            <minimum>1.0</minimum>
                                                            <maximum>1623.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="31">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="32"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="33">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="34" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="35">
                                                        <keyValueMap id="36"/>
                                                    </annotations>
                                                    <attributeDescription id="37">
                                                        <name>label_nullRowsInTable</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>8</index>
                                                    </attributeDescription>
                                                    <constructionDescription>label_nullRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="38">
                                                        <NumericalStatistics id="39">
                                                            <sum>867.0</sum>
                                                            <squaredSum>4617.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="40">
                                                            <sum>867.0</sum>
                                                            <squaredSum>4617.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="41">
                                                            <minimum>0.0</minimum>
                                                            <maximum>9.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="42">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="43"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="44">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="45" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="46">
                                                        <keyValueMap id="47"/>
                                                    </annotations>
                                                    <attributeDescription id="48">
                                                        <name>label_numAncestorAtts</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>9</index>
                                                    </attributeDescription>
                                                    <constructionDescription>label_numAncestorAtts</constructionDescription>
                                                    <statistics class="linked-list" id="49">
                                                        <NumericalStatistics id="50">
                                                            <sum>7178.0</sum>
                                                            <squaredSum>23756.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="51">
                                                            <sum>7178.0</sum>
                                                            <squaredSum>23756.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="52">
                                                            <minimum>2.0</minimum>
                                                            <maximum>4.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="53">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="54"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="55">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="56" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="57">
                                                        <keyValueMap id="58"/>
                                                    </annotations>
                                                    <attributeDescription id="59">
                                                        <name>label_isAlmostDuplicate</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>11</index>
                                                    </attributeDescription>
                                                    <constructionDescription>label_isAlmostDuplicate</constructionDescription>
                                                    <statistics class="linked-list" id="60">
                                                        <NumericalStatistics id="61">
                                                            <sum>395.0</sum>
                                                            <squaredSum>395.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="62">
                                                            <sum>395.0</sum>
                                                            <squaredSum>395.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="63">
                                                            <minimum>0.0</minimum>
                                                            <maximum>1.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="64">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="65"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="66">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="67" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="68">
                                                        <keyValueMap id="69"/>
                                                    </annotations>
                                                    <attributeDescription id="70">
                                                        <name>rra_numRowsInTable</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>14</index>
                                                    </attributeDescription>
                                                    <constructionDescription>rra_numRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="71">
                                                        <NumericalStatistics id="72">
                                                            <sum>53207.0</sum>
                                                            <squaredSum>1.0763847E7</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="73">
                                                            <sum>53207.0</sum>
                                                            <squaredSum>1.0763847E7</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="74">
                                                            <minimum>3.0</minimum>
                                                            <maximum>1623.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="75">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="76"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="77">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="78" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="79">
                                                        <keyValueMap id="80"/>
                                                    </annotations>
                                                    <attributeDescription id="81">
                                                        <name>rra_nullRowsInTable</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>15</index>
                                                    </attributeDescription>
                                                    <constructionDescription>rra_nullRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="82">
                                                        <NumericalStatistics id="83">
                                                            <sum>23184.0</sum>
                                                            <squaredSum>1747902.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="84">
                                                            <sum>23184.0</sum>
                                                            <squaredSum>1747902.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="85">
                                                            <minimum>0.0</minimum>
                                                            <maximum>98.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="86">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="87"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="88">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="89" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="90">
                                                        <keyValueMap id="91"/>
                                                    </annotations>
                                                    <attributeDescription id="92">
                                                        <name>rra_numAncestorAtts</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>16</index>
                                                    </attributeDescription>
                                                    <constructionDescription>rra_numAncestorAtts</constructionDescription>
                                                    <statistics class="linked-list" id="93">
                                                        <NumericalStatistics id="94">
                                                            <sum>6379.0</sum>
                                                            <squaredSum>19415.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="95">
                                                            <sum>6379.0</sum>
                                                            <squaredSum>19415.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="96">
                                                            <minimum>2.0</minimum>
                                                            <maximum>6.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="97">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="98"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="99">
                                        <special>false</special>
                                        <attribute class="NumericalAttribute" id="100" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="101">
                                                        <keyValueMap id="102"/>
                                                    </annotations>
                                                    <attributeDescription id="103">
                                                        <name>rra_isAlmostDuplicate</name>
                                                        <valueType>3</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>18</index>
                                                    </attributeDescription>
                                                    <constructionDescription>rra_isAlmostDuplicate</constructionDescription>
                                                    <statistics class="linked-list" id="104">
                                                        <NumericalStatistics id="105">
                                                            <sum>777.0</sum>
                                                            <squaredSum>777.0</squaredSum>
                                                            <valueCounter>2276</valueCounter>
                                                        </NumericalStatistics>
                                                        <WeightedNumericalStatistics id="106">
                                                            <sum>777.0</sum>
                                                            <squaredSum>777.0</squaredSum>
                                                            <totalWeight>2276.0</totalWeight>
                                                            <count>2276.0</count>
                                                        </WeightedNumericalStatistics>
                                                        <com.rapidminer.example.MinMaxStatistics id="107">
                                                            <minimum>0.0</minimum>
                                                            <maximum>1.0</maximum>
                                                        </com.rapidminer.example.MinMaxStatistics>
                                                        <UnknownStatistics id="108">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="109"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="110">
                                        <special>false</special>
                                        <attribute class="PolynominalAttribute" id="111" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="112">
                                                        <keyValueMap id="113"/>
                                                    </annotations>
                                                    <attributeDescription id="114">
                                                        <name>firstInput_notNullRowsInTable</name>
                                                        <valueType>7</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>22</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_notNullRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="115">
                                                        <NominalStatistics id="116">
                                                            <mode>5</mode>
                                                            <maxCounter>341</maxCounter>
                                                            <scores id="117">
                                                                <long>60</long>
                                                                <long>17</long>
                                                                <long>0</long>
                                                                <long>85</long>
                                                                <long>99</long>
                                                                <long>341</long>
                                                                <long>304</long>
                                                                <long>2</long>
                                                                <long>140</long>
                                                                <long>11</long>
                                                                <long>16</long>
                                                                <long>3</long>
                                                                <long>27</long>
                                                                <long>3</long>
                                                                <long>14</long>
                                                                <long>18</long>
                                                                <long>12</long>
                                                                <long>10</long>
                                                                <long>3</long>
                                                                <long>21</long>
                                                                <long>32</long>
                                                                <long>6</long>
                                                                <long>22</long>
                                                                <long>61</long>
                                                                <long>21</long>
                                                                <long>16</long>
                                                                <long>5</long>
                                                                <long>4</long>
                                                                <long>3</long>
                                                                <long>5</long>
                                                                <long>24</long>
                                                                <long>13</long>
                                                                <long>2</long>
                                                                <long>2</long>
                                                                <long>3</long>
                                                                <long>7</long>
                                                                <long>1</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="118">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="119"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <PolynominalAttribute>
                                                <default>
                                                    <nominalMapping class="PolynominalMapping" id="120">
                                                        <symbolToIndexMap id="121">
                                                            <entry>
                                                                <string>111</string>
                                                                <int>11</int>
                                                            </entry>
                                                            <entry>
                                                                <string>17</string>
                                                                <int>10</int>
                                                            </entry>
                                                            <entry>
                                                                <string>18</string>
                                                                <int>24</int>
                                                            </entry>
                                                            <entry>
                                                                <string>15</string>
                                                                <int>12</int>
                                                            </entry>
                                                            <entry>
                                                                <string>16</string>
                                                                <int>7</int>
                                                            </entry>
                                                            <entry>
                                                                <string>39</string>
                                                                <int>16</int>
                                                            </entry>
                                                            <entry>
                                                                <string>13</string>
                                                                <int>14</int>
                                                            </entry>
                                                            <entry>
                                                                <string>NULL</string>
                                                                <int>2</int>
                                                            </entry>
                                                            <entry>
                                                                <string>14</string>
                                                                <int>33</int>
                                                            </entry>
                                                            <entry>
                                                                <string>11</string>
                                                                <int>19</int>
                                                            </entry>
                                                            <entry>
                                                                <string>12</string>
                                                                <int>1</int>
                                                            </entry>
                                                            <entry>
                                                                <string>21</string>
                                                                <int>31</int>
                                                            </entry>
                                                            <entry>
                                                                <string>20</string>
                                                                <int>15</int>
                                                            </entry>
                                                            <entry>
                                                                <string>369</string>
                                                                <int>32</int>
                                                            </entry>
                                                            <entry>
                                                                <string>40</string>
                                                                <int>36</int>
                                                            </entry>
                                                            <entry>
                                                                <string>105</string>
                                                                <int>20</int>
                                                            </entry>
                                                            <entry>
                                                                <string>81</string>
                                                                <int>17</int>
                                                            </entry>
                                                            <entry>
                                                                <string>60</string>
                                                                <int>22</int>
                                                            </entry>
                                                            <entry>
                                                                <string>1623</string>
                                                                <int>28</int>
                                                            </entry>
                                                            <entry>
                                                                <string>45</string>
                                                                <int>26</int>
                                                            </entry>
                                                            <entry>
                                                                <string>121</string>
                                                                <int>30</int>
                                                            </entry>
                                                            <entry>
                                                                <string>44</string>
                                                                <int>23</int>
                                                            </entry>
                                                            <entry>
                                                                <string>47</string>
                                                                <int>18</int>
                                                            </entry>
                                                            <entry>
                                                                <string>23</string>
                                                                <int>13</int>
                                                            </entry>
                                                            <entry>
                                                                <string>24</string>
                                                                <int>29</int>
                                                            </entry>
                                                            <entry>
                                                                <string>3</string>
                                                                <int>5</int>
                                                            </entry>
                                                            <entry>
                                                                <string>10</string>
                                                                <int>4</int>
                                                            </entry>
                                                            <entry>
                                                                <string>7</string>
                                                                <int>8</int>
                                                            </entry>
                                                            <entry>
                                                                <string>30</string>
                                                                <int>25</int>
                                                            </entry>
                                                            <entry>
                                                                <string>6</string>
                                                                <int>3</int>
                                                            </entry>
                                                            <entry>
                                                                <string>5</string>
                                                                <int>0</int>
                                                            </entry>
                                                            <entry>
                                                                <string>4</string>
                                                                <int>9</int>
                                                            </entry>
                                                            <entry>
                                                                <string>70</string>
                                                                <int>27</int>
                                                            </entry>
                                                            <entry>
                                                                <string>9</string>
                                                                <int>6</int>
                                                            </entry>
                                                            <entry>
                                                                <string>120</string>
                                                                <int>34</int>
                                                            </entry>
                                                            <entry>
                                                                <string>8</string>
                                                                <int>35</int>
                                                            </entry>
                                                            <entry>
                                                                <string>89</string>
                                                                <int>21</int>
                                                            </entry>
                                                        </symbolToIndexMap>
                                                        <indexToSymbolMap id="122">
                                                            <string>5</string>
                                                            <string>12</string>
                                                            <string>NULL</string>
                                                            <string>6</string>
                                                            <string>10</string>
                                                            <string>3</string>
                                                            <string>9</string>
                                                            <string>16</string>
                                                            <string>7</string>
                                                            <string>4</string>
                                                            <string>17</string>
                                                            <string>111</string>
                                                            <string>15</string>
                                                            <string>23</string>
                                                            <string>13</string>
                                                            <string>20</string>
                                                            <string>39</string>
                                                            <string>81</string>
                                                            <string>47</string>
                                                            <string>11</string>
                                                            <string>105</string>
                                                            <string>89</string>
                                                            <string>60</string>
                                                            <string>44</string>
                                                            <string>18</string>
                                                            <string>30</string>
                                                            <string>45</string>
                                                            <string>70</string>
                                                            <string>1623</string>
                                                            <string>24</string>
                                                            <string>121</string>
                                                            <string>21</string>
                                                            <string>369</string>
                                                            <string>14</string>
                                                            <string>120</string>
                                                            <string>8</string>
                                                            <string>40</string>
                                                        </indexToSymbolMap>
                                                    </nominalMapping>
                                                </default>
                                            </PolynominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="123">
                                        <special>false</special>
                                        <attribute class="PolynominalAttribute" id="124" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="125">
                                                        <keyValueMap id="126"/>
                                                    </annotations>
                                                    <attributeDescription id="127">
                                                        <name>firstInput_uniqueRowsInTable</name>
                                                        <valueType>7</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>23</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_uniqueRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="128">
                                                        <NominalStatistics id="129">
                                                            <mode>7</mode>
                                                            <maxCounter>323</maxCounter>
                                                            <scores id="130">
                                                                <long>51</long>
                                                                <long>23</long>
                                                                <long>0</long>
                                                                <long>98</long>
                                                                <long>96</long>
                                                                <long>37</long>
                                                                <long>172</long>
                                                                <long>323</long>
                                                                <long>37</long>
                                                                <long>2</long>
                                                                <long>258</long>
                                                                <long>83</long>
                                                                <long>25</long>
                                                                <long>2</long>
                                                                <long>1</long>
                                                                <long>22</long>
                                                                <long>3</long>
                                                                <long>25</long>
                                                                <long>6</long>
                                                                <long>6</long>
                                                                <long>3</long>
                                                                <long>3</long>
                                                                <long>2</long>
                                                                <long>1</long>
                                                                <long>21</long>
                                                                <long>22</long>
                                                                <long>4</long>
                                                                <long>14</long>
                                                                <long>5</long>
                                                                <long>11</long>
                                                                <long>5</long>
                                                                <long>4</long>
                                                                <long>2</long>
                                                                <long>1</long>
                                                                <long>6</long>
                                                                <long>5</long>
                                                                <long>2</long>
                                                                <long>1</long>
                                                                <long>7</long>
                                                                <long>3</long>
                                                                <long>7</long>
                                                                <long>1</long>
                                                                <long>1</long>
                                                                <long>3</long>
                                                                <long>1</long>
                                                                <long>1</long>
                                                                <long>1</long>
                                                                <long>1</long>
                                                                <long>3</long>
                                                                <long>1</long>
                                                                <long>1</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="131">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="132"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <PolynominalAttribute>
                                                <default>
                                                    <nominalMapping class="PolynominalMapping" id="133">
                                                        <symbolToIndexMap id="134">
                                                            <entry>
                                                                <string>79</string>
                                                                <int>39</int>
                                                            </entry>
                                                            <entry>
                                                                <string>111</string>
                                                                <int>13</int>
                                                            </entry>
                                                            <entry>
                                                                <string>35</string>
                                                                <int>23</int>
                                                            </entry>
                                                            <entry>
                                                                <string>39</string>
                                                                <int>19</int>
                                                            </entry>
                                                            <entry>
                                                                <string>40</string>
                                                                <int>44</int>
                                                            </entry>
                                                            <entry>
                                                                <string>81</string>
                                                                <int>20</int>
                                                            </entry>
                                                            <entry>
                                                                <string>1623</string>
                                                                <int>32</int>
                                                            </entry>
                                                            <entry>
                                                                <string>67</string>
                                                                <int>49</int>
                                                            </entry>
                                                            <entry>
                                                                <string>121</string>
                                                                <int>40</int>
                                                            </entry>
                                                            <entry>
                                                                <string>23</string>
                                                                <int>16</int>
                                                            </entry>
                                                            <entry>
                                                                <string>24</string>
                                                                <int>29</int>
                                                            </entry>
                                                            <entry>
                                                                <string>26</string>
                                                                <int>45</int>
                                                            </entry>
                                                            <entry>
                                                                <string>27</string>
                                                                <int>14</int>
                                                            </entry>
                                                            <entry>
                                                                <string>3</string>
                                                                <int>7</int>
                                                            </entry>
                                                            <entry>
                                                                <string>2</string>
                                                                <int>10</int>
                                                            </entry>
                                                            <entry>
                                                                <string>30</string>
                                                                <int>38</int>
                                                            </entry>
                                                            <entry>
                                                                <string>7</string>
                                                                <int>11</int>
                                                            </entry>
                                                            <entry>
                                                                <string>6</string>
                                                                <int>3</int>
                                                            </entry>
                                                            <entry>
                                                                <string>5</string>
                                                                <int>0</int>
                                                            </entry>
                                                            <entry>
                                                                <string>4</string>
                                                                <int>8</int>
                                                            </entry>
                                                            <entry>
                                                                <string>70</string>
                                                                <int>31</int>
                                                            </entry>
                                                            <entry>
                                                                <string>9</string>
                                                                <int>6</int>
                                                            </entry>
                                                            <entry>
                                                                <string>8</string>
                                                                <int>5</int>
                                                            </entry>
                                                            <entry>
                                                                <string>75</string>
                                                                <int>48</int>
                                                            </entry>
                                                            <entry>
                                                                <string>59</string>
                                                                <int>21</int>
                                                            </entry>
                                                            <entry>
                                                                <string>58</string>
                                                                <int>42</int>
                                                            </entry>
                                                            <entry>
                                                                <string>57</string>
                                                                <int>41</int>
                                                            </entry>
                                                            <entry>
                                                                <string>19</string>
                                                                <int>28</int>
                                                            </entry>
                                                            <entry>
                                                                <string>56</string>
                                                                <int>50</int>
                                                            </entry>
                                                            <entry>
                                                                <string>17</string>
                                                                <int>12</int>
                                                            </entry>
                                                            <entry>
                                                                <string>18</string>
                                                                <int>35</int>
                                                            </entry>
                                                            <entry>
                                                                <string>15</string>
                                                                <int>15</int>
                                                            </entry>
                                                            <entry>
                                                                <string>16</string>
                                                                <int>9</int>
                                                            </entry>
                                                            <entry>
                                                                <string>13</string>
                                                                <int>17</int>
                                                            </entry>
                                                            <entry>
                                                                <string>14</string>
                                                                <int>37</int>
                                                            </entry>
                                                            <entry>
                                                                <string>NULL</string>
                                                                <int>2</int>
                                                            </entry>
                                                            <entry>
                                                                <string>11</string>
                                                                <int>24</int>
                                                            </entry>
                                                            <entry>
                                                                <string>12</string>
                                                                <int>1</int>
                                                            </entry>
                                                            <entry>
                                                                <string>21</string>
                                                                <int>47</int>
                                                            </entry>
                                                            <entry>
                                                                <string>20</string>
                                                                <int>18</int>
                                                            </entry>
                                                            <entry>
                                                                <string>369</string>
                                                                <int>36</int>
                                                            </entry>
                                                            <entry>
                                                                <string>105</string>
                                                                <int>25</int>
                                                            </entry>
                                                            <entry>
                                                                <string>60</string>
                                                                <int>27</int>
                                                            </entry>
                                                            <entry>
                                                                <string>48</string>
                                                                <int>46</int>
                                                            </entry>
                                                            <entry>
                                                                <string>45</string>
                                                                <int>30</int>
                                                            </entry>
                                                            <entry>
                                                                <string>44</string>
                                                                <int>43</int>
                                                            </entry>
                                                            <entry>
                                                                <string>698</string>
                                                                <int>33</int>
                                                            </entry>
                                                            <entry>
                                                                <string>47</string>
                                                                <int>22</int>
                                                            </entry>
                                                            <entry>
                                                                <string>10</string>
                                                                <int>4</int>
                                                            </entry>
                                                            <entry>
                                                                <string>53</string>
                                                                <int>34</int>
                                                            </entry>
                                                            <entry>
                                                                <string>89</string>
                                                                <int>26</int>
                                                            </entry>
                                                        </symbolToIndexMap>
                                                        <indexToSymbolMap id="135">
                                                            <string>5</string>
                                                            <string>12</string>
                                                            <string>NULL</string>
                                                            <string>6</string>
                                                            <string>10</string>
                                                            <string>8</string>
                                                            <string>9</string>
                                                            <string>3</string>
                                                            <string>4</string>
                                                            <string>16</string>
                                                            <string>2</string>
                                                            <string>7</string>
                                                            <string>17</string>
                                                            <string>111</string>
                                                            <string>27</string>
                                                            <string>15</string>
                                                            <string>23</string>
                                                            <string>13</string>
                                                            <string>20</string>
                                                            <string>39</string>
                                                            <string>81</string>
                                                            <string>59</string>
                                                            <string>47</string>
                                                            <string>35</string>
                                                            <string>11</string>
                                                            <string>105</string>
                                                            <string>89</string>
                                                            <string>60</string>
                                                            <string>19</string>
                                                            <string>24</string>
                                                            <string>45</string>
                                                            <string>70</string>
                                                            <string>1623</string>
                                                            <string>698</string>
                                                            <string>53</string>
                                                            <string>18</string>
                                                            <string>369</string>
                                                            <string>14</string>
                                                            <string>30</string>
                                                            <string>79</string>
                                                            <string>121</string>
                                                            <string>57</string>
                                                            <string>58</string>
                                                            <string>44</string>
                                                            <string>40</string>
                                                            <string>26</string>
                                                            <string>48</string>
                                                            <string>21</string>
                                                            <string>75</string>
                                                            <string>67</string>
                                                            <string>56</string>
                                                        </indexToSymbolMap>
                                                    </nominalMapping>
                                                </default>
                                            </PolynominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="136">
                                        <special>false</special>
                                        <attribute class="PolynominalAttribute" id="137" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="138">
                                                        <keyValueMap id="139"/>
                                                    </annotations>
                                                    <attributeDescription id="140">
                                                        <name>firstInput_numRowsInTable</name>
                                                        <valueType>7</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>24</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_numRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="141">
                                                        <NominalStatistics id="142">
                                                            <mode>5</mode>
                                                            <maxCounter>341</maxCounter>
                                                            <scores id="143">
                                                                <long>60</long>
                                                                <long>16</long>
                                                                <long>0</long>
                                                                <long>78</long>
                                                                <long>88</long>
                                                                <long>341</long>
                                                                <long>305</long>
                                                                <long>4</long>
                                                                <long>147</long>
                                                                <long>18</long>
                                                                <long>3</long>
                                                                <long>28</long>
                                                                <long>3</long>
                                                                <long>14</long>
                                                                <long>15</long>
                                                                <long>12</long>
                                                                <long>10</long>
                                                                <long>3</long>
                                                                <long>20</long>
                                                                <long>38</long>
                                                                <long>7</long>
                                                                <long>25</long>
                                                                <long>62</long>
                                                                <long>22</long>
                                                                <long>17</long>
                                                                <long>5</long>
                                                                <long>4</long>
                                                                <long>3</long>
                                                                <long>32</long>
                                                                <long>15</long>
                                                                <long>2</long>
                                                                <long>7</long>
                                                                <long>2</long>
                                                                <long>6</long>
                                                                <long>1</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="144">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="145"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <PolynominalAttribute>
                                                <default>
                                                    <nominalMapping class="PolynominalMapping" id="146">
                                                        <symbolToIndexMap id="147">
                                                            <entry>
                                                                <string>111</string>
                                                                <int>10</int>
                                                            </entry>
                                                            <entry>
                                                                <string>17</string>
                                                                <int>9</int>
                                                            </entry>
                                                            <entry>
                                                                <string>18</string>
                                                                <int>23</int>
                                                            </entry>
                                                            <entry>
                                                                <string>15</string>
                                                                <int>11</int>
                                                            </entry>
                                                            <entry>
                                                                <string>16</string>
                                                                <int>7</int>
                                                            </entry>
                                                            <entry>
                                                                <string>39</string>
                                                                <int>15</int>
                                                            </entry>
                                                            <entry>
                                                                <string>13</string>
                                                                <int>13</int>
                                                            </entry>
                                                            <entry>
                                                                <string>NULL</string>
                                                                <int>2</int>
                                                            </entry>
                                                            <entry>
                                                                <string>11</string>
                                                                <int>18</int>
                                                            </entry>
                                                            <entry>
                                                                <string>12</string>
                                                                <int>1</int>
                                                            </entry>
                                                            <entry>
                                                                <string>21</string>
                                                                <int>29</int>
                                                            </entry>
                                                            <entry>
                                                                <string>20</string>
                                                                <int>14</int>
                                                            </entry>
                                                            <entry>
                                                                <string>369</string>
                                                                <int>30</int>
                                                            </entry>
                                                            <entry>
                                                                <string>40</string>
                                                                <int>32</int>
                                                            </entry>
                                                            <entry>
                                                                <string>105</string>
                                                                <int>19</int>
                                                            </entry>
                                                            <entry>
                                                                <string>81</string>
                                                                <int>16</int>
                                                            </entry>
                                                            <entry>
                                                                <string>60</string>
                                                                <int>21</int>
                                                            </entry>
                                                            <entry>
                                                                <string>1623</string>
                                                                <int>27</int>
                                                            </entry>
                                                            <entry>
                                                                <string>45</string>
                                                                <int>25</int>
                                                            </entry>
                                                            <entry>
                                                                <string>121</string>
                                                                <int>28</int>
                                                            </entry>
                                                            <entry>
                                                                <string>44</string>
                                                                <int>22</int>
                                                            </entry>
                                                            <entry>
                                                                <string>47</string>
                                                                <int>17</int>
                                                            </entry>
                                                            <entry>
                                                                <string>23</string>
                                                                <int>12</int>
                                                            </entry>
                                                            <entry>
                                                                <string>24</string>
                                                                <int>34</int>
                                                            </entry>
                                                            <entry>
                                                                <string>3</string>
                                                                <int>5</int>
                                                            </entry>
                                                            <entry>
                                                                <string>10</string>
                                                                <int>4</int>
                                                            </entry>
                                                            <entry>
                                                                <string>7</string>
                                                                <int>8</int>
                                                            </entry>
                                                            <entry>
                                                                <string>30</string>
                                                                <int>24</int>
                                                            </entry>
                                                            <entry>
                                                                <string>6</string>
                                                                <int>3</int>
                                                            </entry>
                                                            <entry>
                                                                <string>5</string>
                                                                <int>0</int>
                                                            </entry>
                                                            <entry>
                                                                <string>4</string>
                                                                <int>31</int>
                                                            </entry>
                                                            <entry>
                                                                <string>70</string>
                                                                <int>26</int>
                                                            </entry>
                                                            <entry>
                                                                <string>9</string>
                                                                <int>6</int>
                                                            </entry>
                                                            <entry>
                                                                <string>8</string>
                                                                <int>33</int>
                                                            </entry>
                                                            <entry>
                                                                <string>89</string>
                                                                <int>20</int>
                                                            </entry>
                                                        </symbolToIndexMap>
                                                        <indexToSymbolMap id="148">
                                                            <string>5</string>
                                                            <string>12</string>
                                                            <string>NULL</string>
                                                            <string>6</string>
                                                            <string>10</string>
                                                            <string>3</string>
                                                            <string>9</string>
                                                            <string>16</string>
                                                            <string>7</string>
                                                            <string>17</string>
                                                            <string>111</string>
                                                            <string>15</string>
                                                            <string>23</string>
                                                            <string>13</string>
                                                            <string>20</string>
                                                            <string>39</string>
                                                            <string>81</string>
                                                            <string>47</string>
                                                            <string>11</string>
                                                            <string>105</string>
                                                            <string>89</string>
                                                            <string>60</string>
                                                            <string>44</string>
                                                            <string>18</string>
                                                            <string>30</string>
                                                            <string>45</string>
                                                            <string>70</string>
                                                            <string>1623</string>
                                                            <string>121</string>
                                                            <string>21</string>
                                                            <string>369</string>
                                                            <string>4</string>
                                                            <string>40</string>
                                                            <string>8</string>
                                                            <string>24</string>
                                                        </indexToSymbolMap>
                                                    </nominalMapping>
                                                </default>
                                            </PolynominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="149">
                                        <special>false</special>
                                        <attribute class="BinominalAttribute" id="150" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="151">
                                                        <keyValueMap id="152"/>
                                                    </annotations>
                                                    <attributeDescription id="153">
                                                        <name>firstInput_nullRowsInTable</name>
                                                        <valueType>6</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>25</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_nullRowsInTable</constructionDescription>
                                                    <statistics class="linked-list" id="154">
                                                        <NominalStatistics id="155">
                                                            <mode>0</mode>
                                                            <maxCounter>1372</maxCounter>
                                                            <scores id="156">
                                                                <long>1372</long>
                                                                <long>0</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="157">
                                                            <unknownCounter>904</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="158"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <BinominalAttribute>
                                                <default>
                                                    <nominalMapping class="BinominalMapping" id="159">
                                                        <firstValue>0</firstValue>
                                                        <secondValue>NULL</secondValue>
                                                    </nominalMapping>
                                                </default>
                                            </BinominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="160">
                                        <special>false</special>
                                        <attribute class="PolynominalAttribute" id="161" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="162">
                                                        <keyValueMap id="163"/>
                                                    </annotations>
                                                    <attributeDescription id="164">
                                                        <name>firstInput_numAncestorAtts</name>
                                                        <valueType>7</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>26</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_numAncestorAtts</constructionDescription>
                                                    <statistics class="linked-list" id="165">
                                                        <NominalStatistics id="166">
                                                            <mode>1</mode>
                                                            <maxCounter>573</maxCounter>
                                                            <scores id="167">
                                                                <long>381</long>
                                                                <long>573</long>
                                                                <long>0</long>
                                                                <long>450</long>
                                                                <long>7</long>
                                                                <long>2</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="168">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="169"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <PolynominalAttribute>
                                                <default>
                                                    <nominalMapping class="PolynominalMapping" id="170">
                                                        <symbolToIndexMap id="171">
                                                            <entry>
                                                                <string>3</string>
                                                                <int>1</int>
                                                            </entry>
                                                            <entry>
                                                                <string>2</string>
                                                                <int>0</int>
                                                            </entry>
                                                            <entry>
                                                                <string>NULL</string>
                                                                <int>2</int>
                                                            </entry>
                                                            <entry>
                                                                <string>6</string>
                                                                <int>4</int>
                                                            </entry>
                                                            <entry>
                                                                <string>5</string>
                                                                <int>5</int>
                                                            </entry>
                                                            <entry>
                                                                <string>4</string>
                                                                <int>3</int>
                                                            </entry>
                                                        </symbolToIndexMap>
                                                        <indexToSymbolMap id="172">
                                                            <string>2</string>
                                                            <string>3</string>
                                                            <string>NULL</string>
                                                            <string>4</string>
                                                            <string>6</string>
                                                            <string>5</string>
                                                        </indexToSymbolMap>
                                                    </nominalMapping>
                                                </default>
                                            </PolynominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="173">
                                        <special>false</special>
                                        <attribute class="PolynominalAttribute" id="174" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="175">
                                                        <keyValueMap id="176"/>
                                                    </annotations>
                                                    <attributeDescription id="177">
                                                        <name>firstInput_depth</name>
                                                        <valueType>7</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>27</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_depth</constructionDescription>
                                                    <statistics class="linked-list" id="178">
                                                        <NominalStatistics id="179">
                                                            <mode>1</mode>
                                                            <maxCounter>573</maxCounter>
                                                            <scores id="180">
                                                                <long>381</long>
                                                                <long>573</long>
                                                                <long>0</long>
                                                                <long>450</long>
                                                                <long>7</long>
                                                                <long>2</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="181">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="182"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <PolynominalAttribute>
                                                <default>
                                                    <nominalMapping class="PolynominalMapping" id="183">
                                                        <symbolToIndexMap id="184">
                                                            <entry>
                                                                <string>9</string>
                                                                <int>3</int>
                                                            </entry>
                                                            <entry>
                                                                <string>13</string>
                                                                <int>4</int>
                                                            </entry>
                                                            <entry>
                                                                <string>7</string>
                                                                <int>1</int>
                                                            </entry>
                                                            <entry>
                                                                <string>NULL</string>
                                                                <int>2</int>
                                                            </entry>
                                                            <entry>
                                                                <string>11</string>
                                                                <int>5</int>
                                                            </entry>
                                                            <entry>
                                                                <string>5</string>
                                                                <int>0</int>
                                                            </entry>
                                                        </symbolToIndexMap>
                                                        <indexToSymbolMap id="185">
                                                            <string>5</string>
                                                            <string>7</string>
                                                            <string>NULL</string>
                                                            <string>9</string>
                                                            <string>13</string>
                                                            <string>11</string>
                                                        </indexToSymbolMap>
                                                    </nominalMapping>
                                                </default>
                                            </PolynominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="186">
                                        <special>false</special>
                                        <attribute class="BinominalAttribute" id="187" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="188">
                                                        <keyValueMap id="189"/>
                                                    </annotations>
                                                    <attributeDescription id="190">
                                                        <name>firstInput_isDuplicate</name>
                                                        <valueType>6</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>28</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_isDuplicate</constructionDescription>
                                                    <statistics class="linked-list" id="191">
                                                        <NominalStatistics id="192">
                                                            <mode>0</mode>
                                                            <maxCounter>1413</maxCounter>
                                                            <scores id="193">
                                                                <long>1413</long>
                                                                <long>0</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="194">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="195"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <BinominalAttribute>
                                                <default>
                                                    <nominalMapping class="BinominalMapping" id="196">
                                                        <firstValue>0</firstValue>
                                                        <secondValue>NULL</secondValue>
                                                    </nominalMapping>
                                                </default>
                                            </BinominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="197">
                                        <special>false</special>
                                        <attribute class="PolynominalAttribute" id="198" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="199">
                                                        <keyValueMap id="200"/>
                                                    </annotations>
                                                    <attributeDescription id="201">
                                                        <name>firstInput_isAlmostDuplicate</name>
                                                        <valueType>7</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>29</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_isAlmostDuplicate</constructionDescription>
                                                    <statistics class="linked-list" id="202">
                                                        <NominalStatistics id="203">
                                                            <mode>0</mode>
                                                            <maxCounter>1222</maxCounter>
                                                            <scores id="204">
                                                                <long>1222</long>
                                                                <long>191</long>
                                                                <long>0</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="205">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="206"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <PolynominalAttribute>
                                                <default>
                                                    <nominalMapping class="PolynominalMapping" id="207">
                                                        <symbolToIndexMap id="208">
                                                            <entry>
                                                                <string>NULL</string>
                                                                <int>2</int>
                                                            </entry>
                                                            <entry>
                                                                <string>1</string>
                                                                <int>1</int>
                                                            </entry>
                                                            <entry>
                                                                <string>0</string>
                                                                <int>0</int>
                                                            </entry>
                                                        </symbolToIndexMap>
                                                        <indexToSymbolMap id="209">
                                                            <string>0</string>
                                                            <string>1</string>
                                                            <string>NULL</string>
                                                        </indexToSymbolMap>
                                                    </nominalMapping>
                                                </default>
                                            </PolynominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="210">
                                        <special>false</special>
                                        <attribute class="BinominalAttribute" id="211" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="212">
                                                        <keyValueMap id="213"/>
                                                    </annotations>
                                                    <attributeDescription id="214">
                                                        <name>firstInput_totalLength</name>
                                                        <valueType>6</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>30</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_totalLength</constructionDescription>
                                                    <statistics class="linked-list" id="215">
                                                        <NominalStatistics id="216">
                                                            <mode>0</mode>
                                                            <maxCounter>1413</maxCounter>
                                                            <scores id="217">
                                                                <long>1413</long>
                                                                <long>0</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="218">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="219"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <BinominalAttribute>
                                                <default>
                                                    <nominalMapping class="BinominalMapping" id="220">
                                                        <firstValue>0</firstValue>
                                                        <secondValue>NULL</secondValue>
                                                    </nominalMapping>
                                                </default>
                                            </BinominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="221">
                                        <special>false</special>
                                        <attribute class="BinominalAttribute" id="222" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="223">
                                                        <keyValueMap id="224"/>
                                                    </annotations>
                                                    <attributeDescription id="225">
                                                        <name>firstInput_numParents</name>
                                                        <valueType>6</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>31</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_numParents</constructionDescription>
                                                    <statistics class="linked-list" id="226">
                                                        <NominalStatistics id="227">
                                                            <mode>0</mode>
                                                            <maxCounter>1413</maxCounter>
                                                            <scores id="228">
                                                                <long>1413</long>
                                                                <long>0</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="229">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="230"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <BinominalAttribute>
                                                <default>
                                                    <nominalMapping class="BinominalMapping" id="231">
                                                        <firstValue>1</firstValue>
                                                        <secondValue>NULL</secondValue>
                                                    </nominalMapping>
                                                </default>
                                            </BinominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="232">
                                        <special>false</special>
                                        <attribute class="BinominalAttribute" id="233" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="234">
                                                        <keyValueMap id="235"/>
                                                    </annotations>
                                                    <attributeDescription id="236">
                                                        <name>firstInput_generatedByConstantCreator</name>
                                                        <valueType>6</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>32</index>
                                                    </attributeDescription>
                                                    <constructionDescription>firstInput_generatedByConstantCreator</constructionDescription>
                                                    <statistics class="linked-list" id="237">
                                                        <NominalStatistics id="238">
                                                            <mode>0</mode>
                                                            <maxCounter>1413</maxCounter>
                                                            <scores id="239">
                                                                <long>1413</long>
                                                                <long>0</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="240">
                                                            <unknownCounter>863</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="241"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <BinominalAttribute>
                                                <default>
                                                    <nominalMapping class="BinominalMapping" id="242">
                                                        <firstValue>0</firstValue>
                                                        <secondValue>NULL</secondValue>
                                                    </nominalMapping>
                                                </default>
                                            </BinominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="243">
                                        <special>false</special>
                                        <attribute class="PolynominalAttribute" id="244" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="245">
                                                        <keyValueMap id="246"/>
                                                    </annotations>
                                                    <attributeDescription id="247">
                                                        <name>modelLearnerName</name>
                                                        <valueType>7</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>37</index>
                                                    </attributeDescription>
                                                    <constructionDescription>modelLearnerName</constructionDescription>
                                                    <statistics class="linked-list" id="248">
                                                        <NominalStatistics id="249">
                                                            <mode>3</mode>
                                                            <maxCounter>549</maxCounter>
                                                            <scores id="250">
                                                                <long>303</long>
                                                                <long>498</long>
                                                                <long>274</long>
                                                                <long>549</long>
                                                                <long>261</long>
                                                                <long>124</long>
                                                                <long>144</long>
                                                                <long>123</long>
                                                            </scores>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="251">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="252"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <PolynominalAttribute>
                                                <default>
                                                    <nominalMapping class="PolynominalMapping" id="253">
                                                        <symbolToIndexMap id="254">
                                                            <entry>
                                                                <string>FindReplaceLearnerSinglePass</string>
                                                                <int>4</int>
                                                            </entry>
                                                            <entry>
                                                                <string>RMKNNLearner</string>
                                                                <int>7</int>
                                                            </entry>
                                                            <entry>
                                                                <string>ZeroLearner</string>
                                                                <int>2</int>
                                                            </entry>
                                                            <entry>
                                                                <string>WekaLinearRegressionIntegerLearner</string>
                                                                <int>1</int>
                                                            </entry>
                                                            <entry>
                                                                <string>WekaLinearRegressionLearner</string>
                                                                <int>5</int>
                                                            </entry>
                                                            <entry>
                                                                <string>BestColumnLearner</string>
                                                                <int>0</int>
                                                            </entry>
                                                            <entry>
                                                                <string>RMLinearRegressionLearner</string>
                                                                <int>6</int>
                                                            </entry>
                                                            <entry>
                                                                <string>MostCommonValueLearner</string>
                                                                <int>3</int>
                                                            </entry>
                                                        </symbolToIndexMap>
                                                        <indexToSymbolMap id="255">
                                                            <string>BestColumnLearner</string>
                                                            <string>WekaLinearRegressionIntegerLearner</string>
                                                            <string>ZeroLearner</string>
                                                            <string>MostCommonValueLearner</string>
                                                            <string>FindReplaceLearnerSinglePass</string>
                                                            <string>WekaLinearRegressionLearner</string>
                                                            <string>RMLinearRegressionLearner</string>
                                                            <string>RMKNNLearner</string>
                                                        </indexToSymbolMap>
                                                    </nominalMapping>
                                                </default>
                                            </PolynominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                    <AttributeRole id="256">
                                        <special>true</special>
                                        <specialName>label</specialName>
                                        <attribute class="BinominalAttribute" id="257" serialization="custom">
                                            <com.rapidminer.example.table.AbstractAttribute>
                                                <default>
                                                    <annotations id="258">
                                                        <keyValueMap id="259"/>
                                                    </annotations>
                                                    <attributeDescription id="260">
                                                        <name>relationshipMadeAllCorrectPredictions</name>
                                                        <valueType>6</valueType>
                                                        <blockType>1</blockType>
                                                        <defaultValue>0.0</defaultValue>
                                                        <index>38</index>
                                                    </attributeDescription>
                                                    <constructionDescription>gensym</constructionDescription>
                                                    <statistics class="linked-list" id="261">
                                                        <NominalStatistics id="262">
                                                            <mode>-1</mode>
                                                            <maxCounter>0</maxCounter>
                                                        </NominalStatistics>
                                                        <UnknownStatistics id="263">
                                                            <unknownCounter>0</unknownCounter>
                                                        </UnknownStatistics>
                                                    </statistics>
                                                    <transformations id="264"/>
                                                </default>
                                            </com.rapidminer.example.table.AbstractAttribute>
                                            <BinominalAttribute>
                                                <default>
                                                    <nominalMapping class="BinominalMapping" id="265">
                                                        <firstValue>false</firstValue>
                                                        <secondValue>true</secondValue>
                                                    </nominalMapping>
                                                </default>
                                            </BinominalAttribute>
                                        </attribute>
                                    </AttributeRole>
                                </attributes>
                            </attributes>
                        </default>
                    </com.rapidminer.example.set.HeaderExampleSet>
                </headerExampleSet>
            </default>
        </com.rapidminer.operator.AbstractModel>
        <TreeModel>
            <default>
                <root id="266">
                    <children class="linked-list" id="267">
                        <com.rapidminer.operator.learner.tree.Edge id="268">
                            <condition class="com.rapidminer.operator.learner.tree.GreaterSplitCondition" id="269">
                                <attributeName>label_uniqueRowsInTable</attributeName>
                                <value>1.5</value>
                            </condition>
                            <child id="270">
                                <children class="linked-list" id="271">
                                    <com.rapidminer.operator.learner.tree.Edge id="272">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="273">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>0.0</value>
                                            <valueString>BestColumnLearner</valueString>
                                        </condition>
                                        <child id="274">
                                            <children class="linked-list" id="275">
                                                <com.rapidminer.operator.learner.tree.Edge id="276">
                                                    <condition class="com.rapidminer.operator.learner.tree.GreaterSplitCondition" id="277">
                                                        <attributeName>label_numAncestorAtts</attributeName>
                                                        <value>3.5</value>
                                                    </condition>
                                                    <child id="278">
                                                        <children class="linked-list" id="279">
                                                            <com.rapidminer.operator.learner.tree.Edge id="280">
                                                                <condition class="com.rapidminer.operator.learner.tree.GreaterSplitCondition" id="281">
                                                                    <attributeName>label_isAlmostDuplicate</attributeName>
                                                                    <value>0.5</value>
                                                                </condition>
                                                                <child id="282">
                                                                    <children class="linked-list" id="283">
                                                                        <com.rapidminer.operator.learner.tree.Edge id="284">
                                                                            <condition class="com.rapidminer.operator.learner.tree.GreaterSplitCondition" id="285">
                                                                                <attributeName>rra_numAncestorAtts</attributeName>
                                                                                <value>3.5</value>
                                                                            </condition>
                                                                            <child id="286">
                                                                                <children class="linked-list" id="287">
                                                                                    <com.rapidminer.operator.learner.tree.Edge id="288">
                                                                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="289">
                                                                                            <attributeName>firstInput_numAncestorAtts</attributeName>
                                                                                            <value>1.0</value>
                                                                                            <valueString>3</valueString>
                                                                                        </condition>
                                                                                        <child id="290">
                                                                                            <label>false</label>
                                                                                            <children class="linked-list" id="291"/>
                                                                                            <counterMap class="linked-hash-map" id="292">
                                                                                                <entry>
                                                                                                    <string>false</string>
                                                                                                    <int>3</int>
                                                                                                </entry>
                                                                                                <entry>
                                                                                                    <string>true</string>
                                                                                                    <int>0</int>
                                                                                                </entry>
                                                                                            </counterMap>
                                                                                        </child>
                                                                                    </com.rapidminer.operator.learner.tree.Edge>
                                                                                    <com.rapidminer.operator.learner.tree.Edge id="293">
                                                                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="294">
                                                                                            <attributeName>firstInput_numAncestorAtts</attributeName>
                                                                                            <value>3.0</value>
                                                                                            <valueString>4</valueString>
                                                                                        </condition>
                                                                                        <child id="295">
                                                                                            <label>true</label>
                                                                                            <children class="linked-list" id="296"/>
                                                                                            <counterMap class="linked-hash-map" id="297">
                                                                                                <entry>
                                                                                                    <string>false</string>
                                                                                                    <int>1</int>
                                                                                                </entry>
                                                                                                <entry>
                                                                                                    <string>true</string>
                                                                                                    <int>12</int>
                                                                                                </entry>
                                                                                            </counterMap>
                                                                                        </child>
                                                                                    </com.rapidminer.operator.learner.tree.Edge>
                                                                                </children>
                                                                                <counterMap class="linked-hash-map" id="298"/>
                                                                            </child>
                                                                        </com.rapidminer.operator.learner.tree.Edge>
                                                                        <com.rapidminer.operator.learner.tree.Edge id="299">
                                                                            <condition class="com.rapidminer.operator.learner.tree.LessEqualsSplitCondition" id="300">
                                                                                <attributeName>rra_numAncestorAtts</attributeName>
                                                                                <value>3.5</value>
                                                                            </condition>
                                                                            <child id="301">
                                                                                <label>false</label>
                                                                                <children class="linked-list" id="302"/>
                                                                                <counterMap class="linked-hash-map" id="303">
                                                                                    <entry>
                                                                                        <string>false</string>
                                                                                        <int>8</int>
                                                                                    </entry>
                                                                                    <entry>
                                                                                        <string>true</string>
                                                                                        <int>0</int>
                                                                                    </entry>
                                                                                </counterMap>
                                                                            </child>
                                                                        </com.rapidminer.operator.learner.tree.Edge>
                                                                    </children>
                                                                    <counterMap class="linked-hash-map" id="304"/>
                                                                </child>
                                                            </com.rapidminer.operator.learner.tree.Edge>
                                                            <com.rapidminer.operator.learner.tree.Edge id="305">
                                                                <condition class="com.rapidminer.operator.learner.tree.LessEqualsSplitCondition" id="306">
                                                                    <attributeName>label_isAlmostDuplicate</attributeName>
                                                                    <value>0.5</value>
                                                                </condition>
                                                                <child id="307">
                                                                    <label>false</label>
                                                                    <children class="linked-list" id="308"/>
                                                                    <counterMap class="linked-hash-map" id="309">
                                                                        <entry>
                                                                            <string>false</string>
                                                                            <int>50</int>
                                                                        </entry>
                                                                        <entry>
                                                                            <string>true</string>
                                                                            <int>4</int>
                                                                        </entry>
                                                                    </counterMap>
                                                                </child>
                                                            </com.rapidminer.operator.learner.tree.Edge>
                                                        </children>
                                                        <counterMap class="linked-hash-map" id="310"/>
                                                    </child>
                                                </com.rapidminer.operator.learner.tree.Edge>
                                                <com.rapidminer.operator.learner.tree.Edge id="311">
                                                    <condition class="com.rapidminer.operator.learner.tree.LessEqualsSplitCondition" id="312">
                                                        <attributeName>label_numAncestorAtts</attributeName>
                                                        <value>3.5</value>
                                                    </condition>
                                                    <child id="313">
                                                        <label>true</label>
                                                        <children class="linked-list" id="314"/>
                                                        <counterMap class="linked-hash-map" id="315">
                                                            <entry>
                                                                <string>false</string>
                                                                <int>63</int>
                                                            </entry>
                                                            <entry>
                                                                <string>true</string>
                                                                <int>161</int>
                                                            </entry>
                                                        </counterMap>
                                                    </child>
                                                </com.rapidminer.operator.learner.tree.Edge>
                                            </children>
                                            <counterMap class="linked-hash-map" id="316"/>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                    <com.rapidminer.operator.learner.tree.Edge id="317">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="318">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>4.0</value>
                                            <valueString>FindReplaceLearnerSinglePass</valueString>
                                        </condition>
                                        <child id="319">
                                            <label>true</label>
                                            <children class="linked-list" id="320"/>
                                            <counterMap class="linked-hash-map" id="321">
                                                <entry>
                                                    <string>false</string>
                                                    <int>119</int>
                                                </entry>
                                                <entry>
                                                    <string>true</string>
                                                    <int>138</int>
                                                </entry>
                                            </counterMap>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                    <com.rapidminer.operator.learner.tree.Edge id="322">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="323">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>3.0</value>
                                            <valueString>MostCommonValueLearner</valueString>
                                        </condition>
                                        <child id="324">
                                            <label>false</label>
                                            <children class="linked-list" id="325"/>
                                            <counterMap class="linked-hash-map" id="326">
                                                <entry>
                                                    <string>false</string>
                                                    <int>114</int>
                                                </entry>
                                                <entry>
                                                    <string>true</string>
                                                    <int>0</int>
                                                </entry>
                                            </counterMap>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                    <com.rapidminer.operator.learner.tree.Edge id="327">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="328">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>7.0</value>
                                            <valueString>RMKNNLearner</valueString>
                                        </condition>
                                        <child id="329">
                                            <label>false</label>
                                            <children class="linked-list" id="330"/>
                                            <counterMap class="linked-hash-map" id="331">
                                                <entry>
                                                    <string>false</string>
                                                    <int>120</int>
                                                </entry>
                                                <entry>
                                                    <string>true</string>
                                                    <int>0</int>
                                                </entry>
                                            </counterMap>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                    <com.rapidminer.operator.learner.tree.Edge id="332">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="333">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>6.0</value>
                                            <valueString>RMLinearRegressionLearner</valueString>
                                        </condition>
                                        <child id="334">
                                            <label>false</label>
                                            <children class="linked-list" id="335"/>
                                            <counterMap class="linked-hash-map" id="336">
                                                <entry>
                                                    <string>false</string>
                                                    <int>141</int>
                                                </entry>
                                                <entry>
                                                    <string>true</string>
                                                    <int>0</int>
                                                </entry>
                                            </counterMap>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                    <com.rapidminer.operator.learner.tree.Edge id="337">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="338">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>1.0</value>
                                            <valueString>WekaLinearRegressionIntegerLearner</valueString>
                                        </condition>
                                        <child id="339">
                                            <children class="linked-list" id="340">
                                                <com.rapidminer.operator.learner.tree.Edge id="341">
                                                    <condition class="com.rapidminer.operator.learner.tree.GreaterSplitCondition" id="342">
                                                        <attributeName>label_numAncestorAtts</attributeName>
                                                        <value>2.5</value>
                                                    </condition>
                                                    <child id="343">
                                                        <label>false</label>
                                                        <children class="linked-list" id="344"/>
                                                        <counterMap class="linked-hash-map" id="345">
                                                            <entry>
                                                                <string>false</string>
                                                                <int>119</int>
                                                            </entry>
                                                            <entry>
                                                                <string>true</string>
                                                                <int>52</int>
                                                            </entry>
                                                        </counterMap>
                                                    </child>
                                                </com.rapidminer.operator.learner.tree.Edge>
                                                <com.rapidminer.operator.learner.tree.Edge id="346">
                                                    <condition class="com.rapidminer.operator.learner.tree.LessEqualsSplitCondition" id="347">
                                                        <attributeName>label_numAncestorAtts</attributeName>
                                                        <value>2.5</value>
                                                    </condition>
                                                    <child id="348">
                                                        <label>true</label>
                                                        <children class="linked-list" id="349"/>
                                                        <counterMap class="linked-hash-map" id="350">
                                                            <entry>
                                                                <string>false</string>
                                                                <int>13</int>
                                                            </entry>
                                                            <entry>
                                                                <string>true</string>
                                                                <int>137</int>
                                                            </entry>
                                                        </counterMap>
                                                    </child>
                                                </com.rapidminer.operator.learner.tree.Edge>
                                            </children>
                                            <counterMap class="linked-hash-map" id="351"/>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                    <com.rapidminer.operator.learner.tree.Edge id="352">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="353">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>5.0</value>
                                            <valueString>WekaLinearRegressionLearner</valueString>
                                        </condition>
                                        <child id="354">
                                            <label>false</label>
                                            <children class="linked-list" id="355"/>
                                            <counterMap class="linked-hash-map" id="356">
                                                <entry>
                                                    <string>false</string>
                                                    <int>119</int>
                                                </entry>
                                                <entry>
                                                    <string>true</string>
                                                    <int>0</int>
                                                </entry>
                                            </counterMap>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                    <com.rapidminer.operator.learner.tree.Edge id="357">
                                        <condition class="com.rapidminer.operator.learner.tree.NominalSplitCondition" id="358">
                                            <attributeName>modelLearnerName</attributeName>
                                            <value>2.0</value>
                                            <valueString>ZeroLearner</valueString>
                                        </condition>
                                        <child id="359">
                                            <label>false</label>
                                            <children class="linked-list" id="360"/>
                                            <counterMap class="linked-hash-map" id="361">
                                                <entry>
                                                    <string>false</string>
                                                    <int>114</int>
                                                </entry>
                                                <entry>
                                                    <string>true</string>
                                                    <int>0</int>
                                                </entry>
                                            </counterMap>
                                        </child>
                                    </com.rapidminer.operator.learner.tree.Edge>
                                </children>
                                <counterMap class="linked-hash-map" id="362"/>
                            </child>
                        </com.rapidminer.operator.learner.tree.Edge>
                        <com.rapidminer.operator.learner.tree.Edge id="363">
                            <condition class="com.rapidminer.operator.learner.tree.LessEqualsSplitCondition" id="364">
                                <attributeName>label_uniqueRowsInTable</attributeName>
                                <value>1.5</value>
                            </condition>
                            <child id="365">
                                <label>true</label>
                                <children class="linked-list" id="366"/>
                                <counterMap class="linked-hash-map" id="367">
                                    <entry>
                                        <string>false</string>
                                        <int>16</int>
                                    </entry>
                                    <entry>
                                        <string>true</string>
                                        <int>772</int>
                                    </entry>
                                </counterMap>
                            </child>
                        </com.rapidminer.operator.learner.tree.Edge>
                    </children>
                    <counterMap class="linked-hash-map" id="368"/>
                </root>
            </default>
        </TreeModel>
    </TreeModel>
</object-stream>