package ims.chat.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangchen
 * on 2019/4/18 17:27
 */
public class IDCard {
    /**
     * log_id : 6361604210102482866
     * words_result_num : 6
     * direction : 0
     * image_status : normal
     * words_result : {"住址":{"location":{"width":415,"top":366,"height":91,"left":246},"words":"江苏省兴化市合陈镇陆谦村新农20号"},"出生":{"location":{"width":324,"top":282,"height":37,"left":251},"words":"19960504"},"姓名":{"location":{"width":115,"top":120,"height":43,"left":256},"words":"杨辰"},"公民身份号码":{"location":{"width":563,"top":574,"height":45,"left":408},"words":"321281199605041071"},"性别":{"location":{"width":32,"top":207,"height":35,"left":255},"words":"男"},"民族":{"location":{"width":29,"top":210,"height":31,"left":460},"words":"汉"}}
     */

    private long log_id;
    private int words_result_num;
    private int direction;
    private String image_status;
    private WordsResultBean words_result;

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public int getWords_result_num() {
        return words_result_num;
    }

    public void setWords_result_num(int words_result_num) {
        this.words_result_num = words_result_num;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getImage_status() {
        return image_status;
    }

    public void setImage_status(String image_status) {
        this.image_status = image_status;
    }

    public WordsResultBean getWords_result() {
        return words_result;
    }

    public void setWords_result(WordsResultBean words_result) {
        this.words_result = words_result;
    }

    public static class WordsResultBean {
        /**
         * 住址 : {"location":{"width":415,"top":366,"height":91,"left":246},"words":"江苏省兴化市合陈镇陆谦村新农20号"}
         * 出生 : {"location":{"width":324,"top":282,"height":37,"left":251},"words":"19960504"}
         * 姓名 : {"location":{"width":115,"top":120,"height":43,"left":256},"words":"杨辰"}
         * 公民身份号码 : {"location":{"width":563,"top":574,"height":45,"left":408},"words":"321281199605041071"}
         * 性别 : {"location":{"width":32,"top":207,"height":35,"left":255},"words":"男"}
         * 民族 : {"location":{"width":29,"top":210,"height":31,"left":460},"words":"汉"}
         */

        @SerializedName("住址")
        private Address address;
        @SerializedName("出生")
        private BirthDay birthDay;
        @SerializedName("姓名")
        private Name name;
        @SerializedName("公民身份号码")
        private ID id;
        @SerializedName("性别")
        private Sex sex;
        @SerializedName("民族")
        private Nation nation;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public BirthDay getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(BirthDay birthDay) {
            this.birthDay = birthDay;
        }

        public Name getName() {
            return name;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public ID getId() {
            return id;
        }

        public void setId(ID id) {
            this.id = id;
        }

        public Sex getSex() {
            return sex;
        }

        public void setSex(Sex sex) {
            this.sex = sex;
        }

        public Nation getNation() {
            return nation;
        }

        public void setNation(Nation nation) {
            this.nation = nation;
        }

        public static class Address {
            /**
             * location : {"width":415,"top":366,"height":91,"left":246}
             * words : 江苏省兴化市合陈镇陆谦村新农20号
             */

            private LocationBean location;
            private String words;

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBean {
                /**
                 * width : 415
                 * top : 366
                 * height : 91
                 * left : 246
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class BirthDay {
            /**
             * location : {"width":324,"top":282,"height":37,"left":251}
             * words : 19960504
             */

            private LocationBeanX location;
            private String words;

            public LocationBeanX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanX {
                /**
                 * width : 324
                 * top : 282
                 * height : 37
                 * left : 251
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class Name {
            /**
             * location : {"width":115,"top":120,"height":43,"left":256}
             * words : 杨辰
             */

            private LocationBeanXX location;
            private String words;

            public LocationBeanXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXX {
                /**
                 * width : 115
                 * top : 120
                 * height : 43
                 * left : 256
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class ID {
            /**
             * location : {"width":563,"top":574,"height":45,"left":408}
             * words :
             */

            private LocationBeanXXX location;
            private String words;

            public LocationBeanXXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXXX {
                /**
                 * width : 563
                 * top : 574
                 * height : 45
                 * left : 408
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class Sex {
            /**
             * location : {"width":32,"top":207,"height":35,"left":255}
             * words : 男
             */

            private LocationBeanXXXX location;
            private String words;

            public LocationBeanXXXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXXXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXXXX {
                /**
                 * width : 32
                 * top : 207
                 * height : 35
                 * left : 255
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }

        public static class Nation {
            /**
             * location : {"width":29,"top":210,"height":31,"left":460}
             * words : 汉
             */

            private LocationBeanXXXXX location;
            private String words;

            public LocationBeanXXXXX getLocation() {
                return location;
            }

            public void setLocation(LocationBeanXXXXX location) {
                this.location = location;
            }

            public String getWords() {
                return words;
            }

            public void setWords(String words) {
                this.words = words;
            }

            public static class LocationBeanXXXXX {
                /**
                 * width : 29
                 * top : 210
                 * height : 31
                 * left : 460
                 */

                private int width;
                private int top;
                private int height;
                private int left;

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }
            }
        }
    }
}
