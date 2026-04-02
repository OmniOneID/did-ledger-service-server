import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Box, Typography, Chip, Paper } from "@mui/material";
import React, { useEffect, useState } from "react";
import usePasswordPolicy from "../hooks/usePasswordPolicy";
import { ValidationRuleResult } from "../constants/password-policy";

interface TestPasswordDialogProps {
  open: boolean;
  onClose: () => void;
}

const TestPasswordValidationDialog: React.FC<TestPasswordDialogProps> = ({ open, onClose }) => {
  const [testPassword, setTestPassword] = useState("");

  // Password policy hook
  const {
    policy,
    isLoading: isPolicyLoading,
    validatePassword,
    getValidationResults,
    getValidationSummary
  } = usePasswordPolicy();

  // Real-time password validation results
  const [validationResults, setValidationResults] = useState<ValidationRuleResult[]>([]);
  const [validationSummary, setValidationSummary] = useState({
    isValid: false,
    passedCount: 0,
    totalCount: 0,
    failedRules: [] as string[]
  });

  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const password = event.target.value;
    setTestPassword(password);

    // Update real-time validation when policy is available
    if (policy && password) {
      const results = getValidationResults(password);
      const summary = getValidationSummary(password);
      setValidationResults(results);
      setValidationSummary(summary);
    } else {
      setValidationResults([]);
      setValidationSummary({
        isValid: false,
        passedCount: 0,
        totalCount: 0,
        failedRules: []
      });
    }
  };

  // Reset form when dialog opens
  useEffect(() => {
    if (open) {
      setTestPassword("");
      setValidationResults([]);
      setValidationSummary({
        isValid: false,
        passedCount: 0,
        totalCount: 0,
        failedRules: []
      });
    }
  }, [open]);

  // Show loading if policy is being loaded
  if (isPolicyLoading) {
    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
          <DialogContent sx={{ textAlign: 'center', py: 4 }}>
            <Typography>Loading password policy...</Typography>
          </DialogContent>
        </Dialog>
    );
  }

  return (
      <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
        <Box sx={{ px: 3, py: 2 }}>
          <DialogTitle sx={{ p: 0, pt: 1, fontWeight: 700, fontSize: '1.5rem' }}>
            Password Policy Test Tool
          </DialogTitle>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1, mb: 2 }}>
            Test how passwords perform against the current password policy
          </Typography>
        </Box>

        <DialogContent sx={{ px: 3 }}>
          {/* Current Policy Display */}
          {policy && (
              <Paper elevation={1} sx={{ p: 2, mb: 3, bgcolor: 'grey.50' }}>
                <Typography variant="h6" sx={{ mb: 1, fontSize: '1rem', fontWeight: 600 }}>
                  Current Password Policy:
                </Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                  <Chip label={`Min Length: ${policy.minLength}`} size="small" />
                  {policy.requireUppercase && <Chip label="Uppercase Required" size="small" />}
                  {policy.requireLowercase && <Chip label="Lowercase Required" size="small" />}
                  {policy.requireNumber && <Chip label="Numbers Required" size="small" />}
                  {policy.requireSpecial && <Chip label="Special Chars Required" size="small" />}
                </Box>
              </Paper>
          )}

          <TextField
              fullWidth
              label="Test Password"
              type="password"
              variant="outlined"
              margin="normal"
              value={testPassword}
              onChange={handlePasswordChange}
              placeholder="Enter a password to test against the policy..."
              sx={{ mb: 2 }}
          />

          {/* Validation Results */}
          {policy && testPassword && (
              <Box>
                {/* Overall Status */}
                <Box sx={{ mb: 2 }}>
                  <Typography variant="h6" sx={{ mb: 1, fontSize: '1rem', fontWeight: 600 }}>
                    Validation Results:
                  </Typography>
                  <Chip
                      label={validationSummary.isValid ? "✅ Password Valid" : "❌ Password Invalid"}
                      color={validationSummary.isValid ? "success" : "error"}
                      variant="filled"
                      sx={{ mb: 1 }}
                  />
                  <Typography variant="body2" color="text.secondary">
                    {validationSummary.passedCount} of {validationSummary.totalCount} requirements met
                  </Typography>
                </Box>

                {/* Detailed Requirements */}
                <Box sx={{ mb: 2 }}>
                  <Typography variant="h6" sx={{ mb: 1, fontSize: '1rem', fontWeight: 600 }}>
                    Requirements Check:
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                    {validationResults.map((result, index) => (
                        <Chip
                            key={index}
                            label={result.message}
                            size="small"
                            color={result.isValid ? "success" : "default"}
                            variant={result.isValid ? "filled" : "outlined"}
                            sx={{ fontSize: '0.75rem' }}
                        />
                    ))}
                  </Box>
                </Box>

                {/* Failed Rules (if any) */}
                {validationSummary.failedRules.length > 0 && (
                    <Box sx={{ mb: 2 }}>
                      <Typography variant="h6" sx={{ mb: 1, fontSize: '1rem', fontWeight: 600, color: 'error.main' }}>
                        Issues to Fix:
                      </Typography>
                      <Box component="ul" sx={{ pl: 2, m: 0 }}>
                        {validationSummary.failedRules.map((rule, index) => (
                            <Typography key={index} component="li" variant="body2" color="error.main">
                              {rule}
                            </Typography>
                        ))}
                      </Box>
                    </Box>
                )}
              </Box>
          )}

          {/* No Policy Message */}
          {!policy && (
              <Paper elevation={1} sx={{ p: 3, textAlign: 'center', bgcolor: 'warning.light' }}>
                <Typography variant="body1" color="warning.dark">
                  No password policy is currently loaded.
                </Typography>
                <Typography variant="body2" color="warning.dark" sx={{ mt: 1 }}>
                  Please check your password policy configuration.
                </Typography>
              </Paper>
          )}

          {/* Empty Password Message */}
          {policy && !testPassword && (
              <Paper elevation={1} sx={{ p: 3, textAlign: 'center', bgcolor: 'info.light' }}>
                <Typography variant="body1" color="info.dark">
                  Enter a password above to see how it performs against the policy.
                </Typography>
              </Paper>
          )}
        </DialogContent>

        <DialogActions sx={{ px: 3, pt: 0, pb: 2 }}>
          <Button variant="contained" onClick={onClose} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
  );
};

export default TestPasswordValidationDialog;
