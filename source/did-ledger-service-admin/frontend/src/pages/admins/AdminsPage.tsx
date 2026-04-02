import { useEffect } from 'react';
import { useNavigate } from 'react-router';

type Props = {}

const AdminsPage = (props: Props) => {
    const navigate = useNavigate();

    useEffect(() => {
        navigate('admins/admin-management');
    }, [navigate]);

    return null;
}

export default AdminsPage