PGDMP                   	    |            Spotify    17.0    17.0     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            �           1262    16392    Spotify    DATABASE        CREATE DATABASE "Spotify" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Spanish_Colombia.1252';
    DROP DATABASE "Spotify";
                     postgres    false            �            1259    16399    userdata    TABLE     �   CREATE TABLE public.userdata (
    id integer NOT NULL,
    username character varying(11) NOT NULL,
    password character varying(11) NOT NULL
);
    DROP TABLE public.userdata;
       public         heap r       postgres    false            �            1259    16398    userdata_id_seq    SEQUENCE     �   CREATE SEQUENCE public.userdata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.userdata_id_seq;
       public               postgres    false    218            �           0    0    userdata_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.userdata_id_seq OWNED BY public.userdata.id;
          public               postgres    false    217            W           2604    16402    userdata id    DEFAULT     j   ALTER TABLE ONLY public.userdata ALTER COLUMN id SET DEFAULT nextval('public.userdata_id_seq'::regclass);
 :   ALTER TABLE public.userdata ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    218    217    218            �          0    16399    userdata 
   TABLE DATA           :   COPY public.userdata (id, username, password) FROM stdin;
    public               postgres    false    218   �
       �           0    0    userdata_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.userdata_id_seq', 9, true);
          public               postgres    false    217            Y           2606    16404    userdata userdata_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.userdata
    ADD CONSTRAINT userdata_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.userdata DROP CONSTRAINT userdata_pkey;
       public                 postgres    false    218            �   o   x�e�K
�0D�3�)��&�]������IKVeo>��ѷ��iՔ�zQ5�Y�����Qe�����HE¢r���C�9�&ǉd.h���SQ��g����� T�)V     