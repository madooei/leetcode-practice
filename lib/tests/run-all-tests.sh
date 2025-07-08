#!/bin/bash

# Enhanced LeetCode Test Framework - Complete Test Suite Runner
# This script runs all tests from the project root with proper classpaths

echo "=============================================================================="
echo "Enhanced LeetCode Test Framework - Complete Test Suite"
echo "=============================================================================="
echo

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TOTAL_SUITES=0
PASSED_SUITES=0

# Function to run a test suite
run_test_suite() {
    local suite_name="$1"
    local package_path="$2"
    local class_name="$3"
    local description="$4"
    
    echo -e "${BLUE}📋 $suite_name${NC}"
    echo "   $description"
    echo "   Package: $package_path"
    echo
    
    TOTAL_SUITES=$((TOTAL_SUITES + 1))
    
    # Run the test from project root
    echo "   Running tests..."
    if java -cp bin $package_path.$class_name > /dev/null 2>&1; then
        echo -e "   ${GREEN}✅ PASSED${NC}"
        PASSED_SUITES=$((PASSED_SUITES + 1))
    else
        echo -e "   ${RED}❌ FAILED${NC}"
        echo "   Run manually for detailed output:"
        echo "   java -cp bin $package_path.$class_name"
    fi
    
    echo
    return 0
}

# Ensure we're in the project root
if [ ! -d "lib/shared" ] || [ ! -d "lib/tests" ]; then
    echo -e "${RED}Error: Please run this script from the project root directory${NC}"
    echo "Expected structure: lib/shared/ and lib/tests/ should exist"
    exit 1
fi

# Check if classes are compiled
if [ ! -d "bin" ] || [ ! -f "bin/tests/unit/TestFrameworkTests.class" ]; then
    echo -e "${YELLOW}⚠️  Classes not found in bin/. Please compile first using VSCode or:${NC}"
    echo "   javac -cp lib/shared -d bin lib/shared/*.java lib/tests/unit/*.java lib/tests/integration/*.java lib/tests/examples/*.java"
    echo
    exit 1
fi

echo "🧪 Running test suites from project root..."
echo

# 1. Unit Tests
run_test_suite \
    "Unit Tests" \
    "tests.unit" \
    "TestFrameworkTests" \
    "Comprehensive unit tests for all framework components (85+ tests)"

# 2. Integration Tests  
run_test_suite \
    "Integration Tests" \
    "tests.integration" \
    "SimpleTest" \
    "End-to-end workflow testing with real JSON files"

# 3. Example Demonstrations
run_test_suite \
    "Example Demonstrations" \
    "tests.examples" \
    "FinalDemo" \
    "Complete framework showcase with live demonstrations"

# Summary
echo "=============================================================================="
echo "📊 Test Suite Summary"
echo "=============================================================================="
echo
echo "Suites Run: $TOTAL_SUITES"
echo "Passed: $PASSED_SUITES"
echo "Failed: $((TOTAL_SUITES - PASSED_SUITES))"

if [ $PASSED_SUITES -eq $TOTAL_SUITES ]; then
    echo -e "Status: ${GREEN}🎉 ALL TESTS PASSED${NC}"
    echo
    echo "✅ The enhanced LeetCode test framework is working correctly!"
    echo "✅ All components have been validated"
    echo "✅ Backward compatibility is maintained"
    echo "✅ Enhanced features are functional"
    exit 0
else
    echo -e "Status: ${RED}❌ SOME TESTS FAILED${NC}"
    echo
    echo "⚠️  Please review the failed tests above"
    echo "⚠️  Run individual test suites for detailed output"
    echo "⚠️  Check the troubleshooting section in lib/tests/README.md"
    exit 1
fi