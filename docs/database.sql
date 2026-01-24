-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.analytics_events (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  resource_id uuid NOT NULL,
  resource_type text NOT NULL,
  visitor_ip_hash text,
  user_agent text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT analytics_events_pkey PRIMARY KEY (id)
);
CREATE TABLE public.cv_education (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  cv_id uuid NOT NULL,
  degree text NOT NULL,
  institution text NOT NULL,
  start_date date,
  end_date date,
  is_current boolean DEFAULT false,
  description text,
  sort_order integer DEFAULT 0,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT cv_education_pkey PRIMARY KEY (id),
  CONSTRAINT cv_education_cv_id_fkey FOREIGN KEY (cv_id) REFERENCES public.cvs(id)
);
CREATE TABLE public.cv_experience (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  cv_id uuid NOT NULL,
  role text NOT NULL,
  company text NOT NULL,
  start_date date,
  end_date date,
  is_current boolean DEFAULT false,
  description text,
  sort_order integer DEFAULT 0,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT cv_experience_pkey PRIMARY KEY (id),
  CONSTRAINT cv_experience_cv_id_fkey FOREIGN KEY (cv_id) REFERENCES public.cvs(id)
);
CREATE TABLE public.cv_skills (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  cv_id uuid NOT NULL,
  name text NOT NULL,
  proficiency text,
  created_at timestamp with time zone DEFAULT now(),
  sort_order integer DEFAULT 0,
  CONSTRAINT cv_skills_pkey PRIMARY KEY (id),
  CONSTRAINT cv_skills_cv_id_fkey FOREIGN KEY (cv_id) REFERENCES public.cvs(id)
);
CREATE TABLE public.cvs (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  title text NOT NULL,
  professional_summary text,
  is_visible boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT now(),
  template_id text DEFAULT 'standard'::text,
  primary_color text DEFAULT '#000000'::text,
  font_family text DEFAULT 'Inter'::text,
  CONSTRAINT cvs_pkey PRIMARY KEY (id),
  CONSTRAINT cvs_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.dishes (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  menu_id uuid NOT NULL,
  name text NOT NULL,
  price numeric NOT NULL,
  image_url text,
  is_visible boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT now(),
  description text,
  category_id uuid,
  sort_order integer DEFAULT 0,
  CONSTRAINT dishes_pkey PRIMARY KEY (id),
  CONSTRAINT dishes_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.menu_categories(id),
  CONSTRAINT dishes_menu_id_fkey FOREIGN KEY (menu_id) REFERENCES public.menus(id)
);
CREATE TABLE public.invitation_responses (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  invitation_id uuid NOT NULL,
  guest_name text NOT NULL,
  status text NOT NULL DEFAULT 'pending'::text,
  dietary_notes text,
  plus_one boolean DEFAULT false,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT invitation_responses_pkey PRIMARY KEY (id),
  CONSTRAINT invitation_responses_invitation_id_fkey FOREIGN KEY (invitation_id) REFERENCES public.invitations(id)
);
CREATE TABLE public.invitations (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  event_name text NOT NULL,
  event_date timestamp with time zone,
  location text,
  description text,
  is_active boolean DEFAULT true,
  template_id text DEFAULT 'elegant'::text,
  created_at timestamp with time zone DEFAULT now(),
  primary_color text DEFAULT '#000000'::text,
  font_family text DEFAULT 'Inter'::text,
  CONSTRAINT invitations_pkey PRIMARY KEY (id),
  CONSTRAINT invitations_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.menu_categories (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  menu_id uuid NOT NULL,
  name text NOT NULL,
  sort_order integer DEFAULT 0,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT menu_categories_pkey PRIMARY KEY (id),
  CONSTRAINT menu_categories_menu_id_fkey FOREIGN KEY (menu_id) REFERENCES public.menus(id)
);
CREATE TABLE public.menus (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  name text NOT NULL,
  description text,
  is_active boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT now(),
  template_id text DEFAULT 'minimalist'::text,
  primary_color text DEFAULT '#000000'::text,
  font_family text DEFAULT 'Inter'::text,
  logo_url text,
  CONSTRAINT menus_pkey PRIMARY KEY (id),
  CONSTRAINT menus_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.plans (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name text NOT NULL,
  description text,
  price numeric NOT NULL DEFAULT 0,
  currency text DEFAULT 'USD'::text,
  interval text DEFAULT 'month'::text,
  features jsonb DEFAULT '{}'::jsonb,
  is_active boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT plans_pkey PRIMARY KEY (id)
);
CREATE TABLE public.portfolio_items (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  portfolio_id uuid NOT NULL,
  title text NOT NULL,
  description text,
  image_url text,
  project_url text,
  sort_order integer DEFAULT 0,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT portfolio_items_pkey PRIMARY KEY (id),
  CONSTRAINT portfolio_items_portfolio_id_fkey FOREIGN KEY (portfolio_id) REFERENCES public.portfolios(id)
);
CREATE TABLE public.portfolios (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  title text NOT NULL,
  description text,
  is_visible boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT now(),
  template_id text DEFAULT 'minimalist'::text,
  primary_color text DEFAULT '#000000'::text,
  font_family text DEFAULT 'Inter'::text,
  CONSTRAINT portfolios_pkey PRIMARY KEY (id),
  CONSTRAINT portfolios_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.product_categories (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  shop_id uuid NOT NULL,
  name text NOT NULL,
  sort_order integer DEFAULT 0,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT product_categories_pkey PRIMARY KEY (id),
  CONSTRAINT product_categories_shop_id_fkey FOREIGN KEY (shop_id) REFERENCES public.shops(id)
);
CREATE TABLE public.products (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  shop_id uuid NOT NULL,
  name text NOT NULL,
  description text,
  price numeric NOT NULL,
  image_url text,
  is_available boolean DEFAULT true,
  stock integer DEFAULT 0,
  created_at timestamp with time zone DEFAULT now(),
  category_id uuid,
  CONSTRAINT products_pkey PRIMARY KEY (id),
  CONSTRAINT products_shop_id_fkey FOREIGN KEY (shop_id) REFERENCES public.shops(id),
  CONSTRAINT products_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.product_categories(id)
);
CREATE TABLE public.profiles (
  id uuid NOT NULL,
  username text NOT NULL UNIQUE,
  display_name text,
  avatar_url text,
  created_at timestamp with time zone DEFAULT now(),
  social_links jsonb DEFAULT '{}'::jsonb,
  updated_at timestamp with time zone DEFAULT now(),
  CONSTRAINT profiles_pkey PRIMARY KEY (id),
  CONSTRAINT profiles_id_fkey FOREIGN KEY (id) REFERENCES auth.users(id)
);
CREATE TABLE public.shops (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  name text NOT NULL,
  description text,
  is_active boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT now(),
  primary_color text DEFAULT '#000000'::text,
  font_family text DEFAULT 'Inter'::text,
  CONSTRAINT shops_pkey PRIMARY KEY (id),
  CONSTRAINT shops_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.subscriptions (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL UNIQUE,
  plan_id uuid NOT NULL,
  status text NOT NULL DEFAULT 'active'::text,
  current_period_start timestamp with time zone DEFAULT now(),
  current_period_end timestamp with time zone,
  cancel_at_period_end boolean DEFAULT false,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now(),
  CONSTRAINT subscriptions_pkey PRIMARY KEY (id),
  CONSTRAINT subscriptions_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id),
  CONSTRAINT subscriptions_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES public.plans(id)
);