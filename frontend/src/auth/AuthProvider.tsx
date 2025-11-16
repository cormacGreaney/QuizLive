import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { setTokenProvider } from '../features/qms/api';

type Profile = {
  id: number;
  email: string;
  name: string;
  pictureUrl?: string;
  role?: string;
  provider?: string;
} | null;

type AuthCtx = {
  accessToken: string | null;
  refreshToken: string | null;
  profile: Profile;
  setTokens: (a: string, r: string) => void;
  setProfile: (p: Profile) => void;
  logout: () => void;
};

const Ctx = createContext<AuthCtx | null>(null);

export default function AuthProvider({ children }: { children: React.ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(() => localStorage.getItem('accessToken'));
  const [refreshToken, setRefreshToken] = useState<string | null>(() => localStorage.getItem('refreshToken'));
  const [profile, setProfile] = useState<Profile>(() => {
    const raw = localStorage.getItem('profile');
    return raw ? JSON.parse(raw) : null;
  });

  const setTokens = (a: string, r: string) => {
    setAccessToken(a);
    setRefreshToken(r);
    localStorage.setItem('accessToken', a);
    localStorage.setItem('refreshToken', r);
  };

  const logout = () => {
    setAccessToken(null);
    setRefreshToken(null);
    setProfile(null);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('profile');
  };

  useEffect(() => {
    if (profile) localStorage.setItem('profile', JSON.stringify(profile));
  }, [profile]);

  
  useEffect(() => {
    setTokenProvider(() => accessToken);
  }, [accessToken]);

  const value = useMemo(
    () => ({ accessToken, refreshToken, profile, setTokens, setProfile, logout }),
    [accessToken, refreshToken, profile]
  );
  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export const useAuth = () => {
  const v = useContext(Ctx);
  if (!v) throw new Error('useAuth must be used inside <AuthProvider/>');
  return v;
};
